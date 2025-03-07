package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.dto.request.UpdateCartRequestDto;
import com.example.outsourcing.domain.order.dto.response.CartResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import com.example.outsourcing.domain.order.repository.CartItemRepository;
import com.example.outsourcing.domain.order.repository.CartRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private CartService cartService;

    private Long userId, shopId, menuId;
    private User mockUser;
    private Shop mockShop;
    private Menu mockMenu;
    private Cart mockCart;
    private CartItem mockCartItem;
    private AuthUser authUser;

    @BeforeEach
    void setUp(){
        userId = 1L;
        shopId = 1L;
        menuId = 1L;

        // AuthUser 설정
        String userEmail = "user@user.com";
        UserRole userRole = UserRole.USER;
        authUser = new AuthUser(userId, userEmail, userRole);

        // User 생성
        mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", userId);
        ReflectionTestUtils.setField(mockUser, "username", "testUser");

        // Shop 생성
        mockShop = new Shop();
        ReflectionTestUtils.setField(mockShop, "id", shopId);
        ReflectionTestUtils.setField(mockShop, "name", "testShop");

        // Menu 생성
        mockMenu = new Menu();
        ReflectionTestUtils.setField(mockMenu, "id", menuId);
        ReflectionTestUtils.setField(mockMenu, "name", "testMenu");
        ReflectionTestUtils.setField(mockMenu, "price", 10000);

        // 기존 CartItem 생성 (단, 테스트용으로 간단하게 구성)
        mockCartItem = new CartItem(mockMenu, 3);

        // Cart 생성: cartItem 목록 포함, totalPrice 계산 (여기서는 10000*2 = 20000)
        ArrayList<CartItem> cartItems = new ArrayList<>();
        cartItems.add(mockCartItem);
        mockCart = new Cart(mockUser, mockShop, cartItems);
        ReflectionTestUtils.setField(mockCart, "id", 1L);
        mockCart.updateTotalPrice(cartItems);
    }

    @Test
    void 장바구니_조회_성공(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(cartRepository.findCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(mockCart);

        CartResponseDto response = cartService.findCarts(authUser, pageable);
        assertNotNull(response);
        assertEquals(mockCart.getId(), response.getId());
        // CartItemResponseDto 리스트 내 항목 검증
        assertFalse(response.getCartItems().getContent().isEmpty());
    }

    @Test
    void 장바구니_업데이트_신규_생성_성공(){
        // 신규 생성: 기존 cart가 없을 경우
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(menuRepository.findMenuById(menuId)).thenReturn(Optional.of(mockMenu));
        when(shopRepository.findShopById(shopId)).thenReturn(Optional.of(mockShop));
        when(cartRepository.existsCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(false);
        // CartItem 저장 시 모의 리턴값
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(mockCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        UpdateCartRequestDto dto = new UpdateCartRequestDto(menuId, shopId, 3);
        CartResponseDto response = cartService.updateCart(authUser, dto, pageable);

        assertNotNull(response);
        // 신규 생성 시 shopId가 설정되어야 함.
        assertEquals(shopId, response.getShopId());
        // CartItemResponseDto 내용 확인 (수량 3으로 업데이트되었는지)
        assertEquals(3, response.getCartItems().getContent().get(0).getQuantity());
    }

    @Test
    void 장바구니_업데이트_기존_장바구니_가게_다를_경우_업데이트(){
        // 기존 장바구니가 있으나, 요청된 shop과 다른 경우 (장바구니 재생성)
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        // 기존 장바구니가 존재
        when(cartRepository.existsCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(true);
        when(cartRepository.findCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(mockCart);
        when(shopRepository.findShopById(shopId)).thenReturn(Optional.of(mockShop));
        when(menuRepository.findMenuById(menuId)).thenReturn(Optional.of(mockMenu));

        UpdateCartRequestDto dto = new UpdateCartRequestDto(menuId, shopId, 5);
        CartResponseDto response = cartService.updateCart(authUser, dto, pageable);

        assertNotNull(response);
        assertEquals(shopId, response.getShopId());
        // 모든 CartItem의 수량이 5로 업데이트되었는지 확인
        response.getCartItems().getContent().forEach(item ->
                assertEquals(5, item.getQuantity())
        );
    }

    @Test
    void 장바구니_삭제_성공(){
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(cartRepository.findCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(mockCart);

        // 호출 전 삭제 시간은 null
        Object deletedAtBefore = ReflectionTestUtils.getField(mockCart, "deletedAt");
        assertNull(deletedAtBefore);

        cartService.deleteCart(authUser);
        Object deletedAtAfter = ReflectionTestUtils.getField(mockCart, "deletedAt");
        assertNotNull(deletedAtAfter);
    }
}
