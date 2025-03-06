package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.service.OrderService;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    // Auth 관련 request attribute를 설정하는 헬퍼
    private void addAuthAttributes(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        builder.requestAttr("userId", 1L)
                .requestAttr("email", "user@user.com")
                .requestAttr("userRole", "USER");
    }

    // dummy OrderResponseDto 생성 (PageRequest.of(0, 10) 전달)
    private OrderResponseDto createOrderResponseDto(OrderState state, CancelReason reason) {
        // User 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "username", "testUser");
        ReflectionTestUtils.setField(user, "role", com.example.outsourcing.common.enums.UserRole.USER);

        // Shop 생성
        Shop shop = new Shop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        ReflectionTestUtils.setField(shop, "name", "testShop");

        // Menu 생성
        Menu menu = new Menu();
        ReflectionTestUtils.setField(menu, "id", 1L);
        ReflectionTestUtils.setField(menu, "name", "testMenu");
        ReflectionTestUtils.setField(menu, "price", 10000);
        ReflectionTestUtils.setField(menu, "shop", shop);

        // CartItem 및 Cart 생성
        CartItem cartItem = new CartItem(menu, 2);
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        Cart cart = new Cart(user, shop, cartItems);
        ReflectionTestUtils.setField(cart, "id", 1L);
        cart.updateTotalPrice(cartItems);

        // Order 생성 (Cart 기반)
        Order order = new Order(state, user, cart);
        ReflectionTestUtils.setField(order, "id", 1L);
        ReflectionTestUtils.setField(order, "reason", reason);

        return new OrderResponseDto(order, PageRequest.of(0, 10));
    }

    @Test
    void 주문_생성_테스트() throws Exception {
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.CLIENT_ACCEPT, null);

        given(orderService.createOrder(any(), any()))
                .willReturn(responseDto);

        // POST /orders 호출 시, AuthUser 관련 attribute들을 설정
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = post("/orders")
                .contentType(MediaType.APPLICATION_JSON);
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("CLIENT_ACCEPT"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void 주문_단건_조회_테스트() throws Exception {
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.CLIENT_ACCEPT, null);

        given(orderService.findOrder(any(), anyLong(), any()))
                .willReturn(responseDto);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = get("/orders/1");
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("CLIENT_ACCEPT"));
    }

    @Test
    void 주문_목록_조회_테스트() throws Exception {
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.CLIENT_ACCEPT, null);
        PageResponseDto<OrderResponseDto> pageResponseDto = new PageResponseDto<>(
                new PageImpl<>(Collections.singletonList(responseDto))
        );

        given(orderService.findOrders(any(), any(), any(), any()))
                .willReturn(pageResponseDto);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = get("/orders")
                .param("shopId", "1")
                .param("orderState", "CLIENT_ACCEPT")
                .param("page", "1")
                .param("size", "10");
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].state").value("CLIENT_ACCEPT"))
                .andExpect(jsonPath("$.reason").doesNotExist());
    }

    @Test
    void 주문_업데이트_테스트() throws Exception {
        // 예: 클라이언트가 취소 요청하여 상태가 OWNER_CANCEL로 변경되는 경우
        com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto updateDto =
                new com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto(false, CancelReason.USER_REQUEST);
        OrderResponseDto responseDto = createOrderResponseDto(OrderState.OWNER_CANCEL, updateDto.getReason());

        given(orderService.updateOrder(any(), anyLong(), anyLong(), any(), any()))
                .willReturn(responseDto);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = patch("/orders/1")
                .param("shopId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.state").value("OWNER_CANCEL"))
                .andExpect(jsonPath("$.reason").value("USER_REQUEST"));
    }
}
