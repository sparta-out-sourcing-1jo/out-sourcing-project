package com.example.outsourcing.common.aop;

import com.example.outsourcing.common.config.CancelReasonConverter;
import com.example.outsourcing.common.config.OrderStateConverter;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.order.controller.OrderController;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerAopLoggingTest {

    @InjectMocks
    private OrderController orderController;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 헬퍼: 테스트용 OrderResponseDto 생성 (AOP 로깅은 요청 파라미터에서 shopId와 PathVariable "orderId"를 추출하므로, 테스트 요청에서 이 값들을 설정합니다.)
    private OrderResponseDto createTestOrderResponse(OrderState state, CancelReason reason) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "username", "testUser");
        ReflectionTestUtils.setField(user, "role", UserRole.USER);

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "testShop");
        ReflectionTestUtils.setField(shop, "user", user);

        // Menu 엔티티 생성
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "name", "testMenu");
        ReflectionTestUtils.setField(menu, "price", 10000.0);
        ReflectionTestUtils.setField(menu, "shop", shop);

        // Order 엔티티 생성 후 id 설정
        Order order = new Order(state, user, shop, menu);
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "reason", reason);
        ReflectionTestUtils.setField(order, "user", user);
        ReflectionTestUtils.setField(order, "shop", shop);

        return new OrderResponseDto(order);
    }

    @Test
    void 주문_업데이트_및_AOP_로깅_테스트(CapturedOutput output) throws Exception {
        // given
        // 클라이언트가 취소 요청하는 경우: isProceed=false, reason=USER_REQUEST
        UpdateOrderRequestDto updateDto = new UpdateOrderRequestDto(false, new CancelReasonConverter().convert("USER_REQUEST"));
        OrderResponseDto responseDto = createTestOrderResponse(new OrderStateConverter().convert("CLIENT_CANCEL"), updateDto.getReason());

        given(orderService.updateOrder(any(UserRole.class), anyLong(), anyLong(), any(UpdateOrderRequestDto.class)))
                .willReturn(responseDto);

        // when: PATCH 요청 실행 (요청 URL의 {orderId} 값과 쿼리 파라미터로 shopId, role를 전달)
        mockMvc.perform(patch("/orders/1")
                        .param("role", "USER")
                        .param("shopId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("CLIENT_CANCEL"))
                .andExpect(jsonPath("$.reason").value("USER_REQUEST"));

        // then: AOP 로깅이 제대로 동작하여 로그 메시지에 shopId와 orderId가 기록되었는지 확인
        String logOutput = output.toString();
        // 로그 메시지는 "[Order API Access] shopId: {shopId}, orderId: {orderId}, requestTime: {timestamp}" 형식임
        // 예시로 "shopId: 1"과 "orderId: 1" 문자열이 포함되었는지 확인
        assertTrue(logOutput.contains("shopId: 1"));
        assertTrue(logOutput.contains("orderId: 1"));
    }
}
