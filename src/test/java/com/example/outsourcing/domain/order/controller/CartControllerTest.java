package com.example.outsourcing.domain.order.controller;

import com.example.outsourcing.domain.order.dto.request.UpdateCartRequestDto;
import com.example.outsourcing.domain.order.dto.response.CartResponseDto;
import com.example.outsourcing.domain.order.service.CartService;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartController.class)
public class CartControllerTest {

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    // Auth 관련 request attribute를 설정하는 헬퍼
    private void addAuthAttributes(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        builder.requestAttr("userId", 1L)
                .requestAttr("email", "user@user.com")
                .requestAttr("userRole", "USER");
    }

    // dummy CartResponseDto 생성 (PageRequest.of(0, 10) 전달)
    private CartResponseDto createCartResponseDto() {
        // User 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "username", "testUser");

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

        // CartItem 생성 및 목록 구성
        CartItem cartItem = new CartItem(menu, 2);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        // Cart 생성
        Cart cart = new Cart(user, shop, cartItems);
        ReflectionTestUtils.setField(cart, "id", 1L);
        cart.updateTotalPrice(cartItems);

        return new CartResponseDto(cart, PageRequest.of(0, 10));
    }

    @Test
    void 장바구니_조회_테스트() throws Exception {
        CartResponseDto responseDto = createCartResponseDto();
        given(cartService.findCarts(any(AuthUser.class), any()))
                .willReturn(responseDto);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = get("/cart");
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shopId").value(1));
    }

    @Test
    void 장바구니_업데이트_테스트() throws Exception {
        UpdateCartRequestDto updateDto = new UpdateCartRequestDto(1L, 1L, 3);
        CartResponseDto responseDto = createCartResponseDto();

        given(cartService.updateCart(any(AuthUser.class), any(UpdateCartRequestDto.class), any()))
                .willReturn(responseDto);

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = patch("/cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto));
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shopId").value(1));
    }

    @Test
    void 장바구니_삭제_테스트() throws Exception {
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder = delete("/cart");
        addAuthAttributes(requestBuilder);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }
}
