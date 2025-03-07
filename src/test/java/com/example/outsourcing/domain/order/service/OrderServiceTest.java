package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    private Long userId, shopId, menuId;
    private User mockUser;
    private Shop mockShop;
    private Menu mockMenu;
    private Cart mockCart;
    private Order mockOrder;
    private AuthUser authUser;      // 기본 USER 권한
    private AuthUser ownerAuthUser; // OWNER 권한

    @BeforeEach
    void setUp(){
        userId = 1L;
        shopId = 1L;
        menuId = 1L;

        // AuthUser 설정
        String userEmail = "user@user.com";
        UserRole userRole = UserRole.USER;
        authUser = new AuthUser(userId, userEmail, userRole);

        Long ownerId = 2L;
        String ownerEmail = "owner@owner.com";
        UserRole ownerRole = UserRole.OWNER;
        ownerAuthUser = new AuthUser(ownerId, ownerEmail, ownerRole);

        // User 생성
        mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", userId);
        ReflectionTestUtils.setField(mockUser, "username", "testUser");
        ReflectionTestUtils.setField(mockUser, "role", UserRole.USER);

        // Shop 생성 (상태 OPEN, 최소 주문 금액 10000)
        mockShop = new Shop();
        ReflectionTestUtils.setField(mockShop, "id", shopId);
        ReflectionTestUtils.setField(mockShop, "name", "testShop");
        ReflectionTestUtils.setField(mockShop, "minPrice", 10000);
        ReflectionTestUtils.setField(mockShop, "state", ShopState.OPEN);
        ReflectionTestUtils.setField(mockShop, "user", mockUser);

        // Menu 생성
        mockMenu = new Menu();
        ReflectionTestUtils.setField(mockMenu, "id", menuId);
        ReflectionTestUtils.setField(mockMenu, "name", "testMenu");
        ReflectionTestUtils.setField(mockMenu, "price", 10000);
        ReflectionTestUtils.setField(mockMenu, "shop", mockShop);

        // Cart 생성 (총액 15000, 빈 cartItem 목록)
        mockCart = new Cart(mockUser, mockShop, new ArrayList<>());
        ReflectionTestUtils.setField(mockCart, "id", 1L);
        ReflectionTestUtils.setField(mockCart, "totalPrice", 15000);

        // Order 생성 (생성 시에는 Cart를 이용)
        mockOrder = new Order(OrderState.CLIENT_ACCEPT, mockUser, mockCart);
        ReflectionTestUtils.setField(mockOrder, "id", 1L);
        // 초기 deletedAt은 null
        ReflectionTestUtils.setField(mockOrder, "deletedAt", null);
    }

    @Test
    void 주문을_생성할_수_있다(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        // given
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(cartRepository.findCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(mockCart);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // when
        OrderResponseDto response = orderService.createOrder(authUser, pageable);

        // then
        assertNotNull(response);
        assertEquals(OrderState.CLIENT_ACCEPT, response.getState());
        assertEquals(mockUser.getId(), response.getUserId());
        assertEquals(mockShop.getId(), response.getShopId());
        assertEquals(mockOrder.getId(), response.getId());
    }

    @Test
    void 주문_생성_시_최소주문금액_미달_예외_발생(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(cartRepository.findCartByUserAndDeletedAtIsNull(mockUser)).thenReturn(mockCart);

        // 최소 주문 금액보다 높은 totalPrice를 강제로 낮춤
        ReflectionTestUtils.setField(mockCart, "totalPrice", 5000);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> orderService.createOrder(authUser, pageable));
        assertTrue(e.getMessage().contains("최소 주문 가격"));
    }

    @Test
    void 주문_단건_조회_성공(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.of(mockOrder));

        OrderResponseDto response = orderService.findOrder(authUser, mockOrder.getId(), pageable);
        assertNotNull(response);
        assertEquals(mockOrder.getId(), response.getId());
    }

    @Test
    void 주문_단건_조회_권한_없음_예외_발생(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        // 다른 사용자로 조회
        Long otherId = 999L;
        String otherEmail = "other@other.com";
        UserRole otherRole = UserRole.USER;
        AuthUser otherUser = new AuthUser(otherId,otherEmail,otherRole);

        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.of(mockOrder));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.findOrder(otherUser, mockOrder.getId(), pageable));
        assertNotNull(ex.getMessage());
    }

    @Test
    void 주문_다건_조회_성공_사용자(){
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        // 사용자일 경우 주문 조회 : findOrdersByUser or findOrdersByUserAndState
        when(userRepository.findUserById(authUser.getId())).thenReturn(Optional.of(mockUser));
        when(orderRepository.findOrdersByUserAndState(any(User.class), any(OrderState.class)))
                .thenReturn(Collections.singletonList(mockOrder));

        PageResponseDto<OrderResponseDto> response =
                orderService.findOrders(authUser, OrderState.CLIENT_ACCEPT, null, pageable);
        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertEquals(mockOrder.getId(), response.getContent().get(0).getId());
    }

    @Test
    void 주문갱신_클라이언트_취소_요청_성공(){
        // 초기 상태 CLIENT_ACCEPT, 고객 취소(isProceed = false)
        ReflectionTestUtils.setField(mockOrder, "state", OrderState.CLIENT_ACCEPT);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));

        // 주문 조회
        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.of(mockOrder));
        // DTO: isProceed false, 취소 사유 제공
        UpdateOrderRequestDto dto = new UpdateOrderRequestDto(false, CancelReason.USER_REQUEST);

        OrderResponseDto response = orderService.updateOrder(authUser, shopId, mockOrder.getId(), dto, pageable);
        assertEquals(OrderState.CLIENT_CANCEL, response.getState());
        Object deletedAt = ReflectionTestUtils.getField(mockOrder, "deletedAt");
        assertNotNull(deletedAt);
    }

    @Test
    void 주문갱신_가게사장_승인_요청_성공(){
        // 초기 상태 CLIENT_ACCEPT, 사장 승인(isProceed = true)
        ReflectionTestUtils.setField(mockOrder, "state", OrderState.CLIENT_ACCEPT);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));

        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.of(mockOrder));
        UpdateOrderRequestDto dto = new UpdateOrderRequestDto(true, null);

        OrderResponseDto response = orderService.updateOrder(ownerAuthUser, shopId, mockOrder.getId(), dto, pageable);
        assertEquals(OrderState.OWNER_ACCEPT, response.getState());
    }

    @Test
    void 주문갱신_클라이언트_최종_승인_요청_성공(){
        // 초기 상태 OWNER_ACCEPT, 고객 승인(isProceed = true)
        ReflectionTestUtils.setField(mockOrder, "state", OrderState.OWNER_ACCEPT);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));

        when(orderRepository.findById(mockOrder.getId())).thenReturn(Optional.of(mockOrder));
        UpdateOrderRequestDto dto = new UpdateOrderRequestDto(true, null);

        OrderResponseDto response = orderService.updateOrder(authUser, shopId, mockOrder.getId(), dto, pageable);
        assertEquals(OrderState.FINISH, response.getState());
    }
}
