package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.order.dto.request.CreateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    // 헬퍼 메서드: 테스트용 OrderResponseDto를 생성하기 위해 Order 엔티티를 생성 후 DTO 변환
    private OrderResponseDto createOrderResponseDto(OrderState state, CancelReason reason) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "username", "testUser");
        ReflectionTestUtils.setField(user, "role", UserRole.USER);

        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "testShop");

        // Menu 엔티티 생성
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "name", "testMenu");
        ReflectionTestUtils.setField(menu, "price", 10000.0);

        // Order 엔티티 생성 후 id 설정
        Order order = new Order(state, user, shop, menu);
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "reason", reason);

        return new OrderResponseDto(order);
    }

    @Test
    void 주문_생성_테스트() throws Exception {
        // given
        CreateOrderRequestDto createDto = new CreateOrderRequestDto(1L, 1L);
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.CLIENT_ACCEPT, null);

        given(orderService.createOrder(anyLong(), any(UserRole.class), any(CreateOrderRequestDto.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/orders")
                        .param("userId", "1")
                        .param("role", "USER")
                        .contentType(MediaType.APPLICATION_JSON)   // 요청 본문이 JSON임을 명시
                        .content(objectMapper.writeValueAsString(createDto)))  // 객체를 JSON 문자열로 변환하여 전송
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("CLIENT_ACCEPT"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void 주문_단건_조회_테스트() throws Exception {
        // given
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.CLIENT_ACCEPT, null);

        given(orderService.findOrder(anyLong(), any(UserRole.class), anyLong()))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/orders/1")
                        .param("userId", "1")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("CLIENT_ACCEPT"));
    }

    @Test
    void 주문_목록_조회_테스트() throws Exception {
        // given
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.CLIENT_ACCEPT, null);
        PageResponseDto<OrderResponseDto> pageResponseDto = new PageResponseDto<>(
                new PageImpl<>(Collections.singletonList(responseDto))
        );

        given(orderService.findOrders(anyLong(), any(UserRole.class), any(), any(OrderState.class), any(Pageable.class)))
                .willReturn(pageResponseDto);

        // when & then
        mockMvc.perform(get("/orders")
                        .param("userId", "1")
                        .param("role", "USER")
                        .param("shopId", "1")
                        .param("orderState", "CLIENT_ACCEPT")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].state").value("CLIENT_ACCEPT"))
                .andExpect(jsonPath("$.reason").doesNotExist());
    }

    @Test
    void 주문_업데이트_테스트() throws Exception {
        // given
        // 예: 클라이언트가 취소 요청하여 상태가 USER_CANCEL로 변경되는 경우
        UpdateOrderRequestDto updateDto = new UpdateOrderRequestDto(false, CancelReason.USER_REQUEST);
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.OWNER_CANCEL, updateDto.getReason());

        given(orderService.updateOrder(any(UserRole.class), anyLong(), anyLong(), any(UpdateOrderRequestDto.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/orders/1")
                        .param("role", "USER")
                        .param("shopId", "1")
                        .contentType(MediaType.APPLICATION_JSON)   // 요청 본문의 데이터 타입을 JSON으로 지정
                        .content(objectMapper.writeValueAsString(updateDto)))  // updateDto 객체를 JSON 문자열로 변환하여 전송
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("OWNER_CANCEL"))
                .andExpect(jsonPath("$.reason").value("USER_REQUEST"));
    }
}
