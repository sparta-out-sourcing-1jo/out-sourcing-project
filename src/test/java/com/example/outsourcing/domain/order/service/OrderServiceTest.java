package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.dto.request.CreateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.OrderRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @InjectMocks
    private OrderService orderService;

    private Long userId, shopId, menuId;
    private CreateOrderRequestDto dto;
    private User mockUser;
    private Shop mockShop;
    private Menu mockMenu;
    private Order mockOrder;

    @BeforeEach
    void setUp(){
        userId = 1L;
        shopId = 1L;
        menuId = 1L;
        dto = new CreateOrderRequestDto(menuId, shopId);

        mockUser = new User();
        ReflectionTestUtils.setField(mockUser, "id", userId);
        ReflectionTestUtils.setField(mockUser, "username", "testUser");
        ReflectionTestUtils.setField(mockUser, "role", UserRole.USER);

        mockShop = new Shop();
        ReflectionTestUtils.setField(mockShop, "id", shopId);
        ReflectionTestUtils.setField(mockShop, "name", "testShop");
        ReflectionTestUtils.setField(mockShop, "minPrice", 10000.0);
        ReflectionTestUtils.setField(mockShop, "user", mockUser);

        mockMenu = new Menu();
        ReflectionTestUtils.setField(mockMenu, "name", "testMenu");
        ReflectionTestUtils.setField(mockMenu, "price", 10000.0);
        ReflectionTestUtils.setField(mockMenu, "shop", mockShop);

        mockOrder = new Order(OrderState.CLIENT_ACCEPT, mockUser, mockShop, mockMenu);
        ReflectionTestUtils.setField(mockOrder, "id", 1L);
    }

    @Test
    void 주문을_생성할_수_있다(){
        //given
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(mockUser));
        when(shopRepository.findShopById(anyLong())).thenReturn(Optional.of(mockShop));
        when(menuRepository.findMenuById(anyLong())).thenReturn(Optional.of(mockMenu));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        //when
        OrderResponseDto createOrder = orderService.createOrder(userId, UserRole.USER, dto);

        //then
        assertNotNull(createOrder);
        assertEquals(OrderState.CLIENT_ACCEPT, createOrder.getState());
        assertEquals(userId, createOrder.getUserId());
        assertEquals(mockShop.getId(), createOrder.getShopId());
        assertEquals(mockMenu.getName(), createOrder.getMenuName());
        assertEquals(mockMenu.getPrice(), createOrder.getTotalPrice());
        assertEquals(mockUser.getId(), createOrder.getUserId());
        assertEquals(mockOrder.getId(), createOrder.getId());
    }

    @Test
    void 주문_생성_시_Exception_발생_상황(){
        //given
        when(userRepository.findUserById(anyLong())).thenReturn(Optional.of(mockUser));
        when(shopRepository.findShopById(anyLong())).thenReturn(Optional.of(mockShop));
        when(menuRepository.findMenuById(anyLong())).thenReturn(Optional.of(mockMenu));

        //when & then
        ReflectionTestUtils.setField(mockShop, "minPrice", 15000.0);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(userId, UserRole.USER, dto));
        assertEquals("400 BAD_REQUEST \"최소 주문 가격을 넘겨주세요.\"", e.getMessage());

        ReflectionTestUtils.setField(mockShop, "minPrice", 10000.0);
        given(menuRepository.findMenuById(anyLong())).willReturn(Optional.empty());
        e = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(userId, UserRole.USER, dto));
        assertEquals("404 NOT_FOUND \"해당 메뉴를 찾을 수 없습니다.\"", e.getMessage());

        given(shopRepository.findShopById(anyLong())).willReturn(Optional.empty());
        e = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(userId, UserRole.USER, dto));
        assertEquals("404 NOT_FOUND \"해당 가게를 찾을 수 없습니다\"", e.getMessage());

        given(userRepository.findUserById(anyLong())).willReturn(Optional.empty());
        e = assertThrows(ResponseStatusException.class, () -> orderService.createOrder(userId, UserRole.USER, dto));
        assertEquals("404 NOT_FOUND \"해당하는 유저를 찾을 수 없습니다.\"", e.getMessage());
    }

    @Test
    void 주문_단건_조회_성공() {
        // arrange
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        // act
        OrderResponseDto response = orderService.findOrder(userId, UserRole.USER, mockOrder.getId());

        // assert
        assertNotNull(response);
        assertEquals(mockOrder.getId(), response.getId());
    }

    @Test
    void 주문_단건_조회_Exception_발생() {
        // arrange
        // 임의의 다른 userId로 호출 (mockOrder의 user나 shop.user와 일치하지 않음)
        Long unauthorizedUserId = 999L;
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));

        // act & assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.findOrder(unauthorizedUserId, UserRole.USER, mockOrder.getId()));
        assertNotNull(ex.getMessage());
    }

    // ---------- findOrders 테스트 ----------
    @Test
    void 주문_다건_조회_성공() {
        // arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        // 단일 주문 리스트를 Page로 감싸기
        Page<Order> page = new PageImpl<>(Collections.singletonList(mockOrder), pageable, 1);
        // 사용자일 경우 findOrdersByUserAndState(...) 호출 (예시)
        when(orderRepository.findOrdersByUserAndState(any(User.class), any(OrderState.class), any(Pageable.class)))
                .thenReturn(page);
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(mockUser));

        // act
        PageResponseDto<OrderResponseDto> response = orderService.findOrders(userId, UserRole.USER, null, OrderState.CLIENT_ACCEPT, pageable);

        // assert
        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertEquals(mockOrder.getId(), response.getContent().get(0).getId());
    }

    // ---------- updateOrder 테스트 ----------
    @Test
    void 주문갱신_클라이언트_취소_요청_성공() {
        // arrange: 초기 상태 CLIENT_ACCEPT, 사용자 취소 요청(isProceed = false)
        ReflectionTestUtils.setField(mockOrder, "state", OrderState.CLIENT_ACCEPT);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        UpdateOrderRequestDto dto = new UpdateOrderRequestDto(false, CancelReason.USER_REQUEST);

        // act
        OrderResponseDto response = orderService.updateOrder(UserRole.USER, shopId, mockOrder.getId(), dto);

        // assert: 상태가 CLIENT_CANCEL로 변경되고, 삭제 시간(deletedAt)이 설정되었는지 확인
        assertEquals(OrderState.CLIENT_CANCEL, response.getState());
        Object deletedAt = ReflectionTestUtils.getField(mockOrder, "deletedAt");
        assertNotNull(deletedAt);
    }

    @Test
    void 주문갱신_가게사장_승인_요청_성공() {
        // arrange: 초기 상태 CLIENT_ACCEPT, 가게 사장이 승인 요청(isProceed = true)
        ReflectionTestUtils.setField(mockOrder, "state", OrderState.CLIENT_ACCEPT);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        // DTO에서 isProceed true, reason은 사용되지 않음
        UpdateOrderRequestDto dto = new UpdateOrderRequestDto(true, null);

        // act: 가게 사장이 처리하면 상태가 OWNER_ACCEPT로 변경
        OrderResponseDto response = orderService.updateOrder(UserRole.OWNER, shopId, mockOrder.getId(), dto);

        // assert
        assertEquals(OrderState.OWNER_ACCEPT, response.getState());
    }

    @Test
    void 주문갱신_클라이언트_최종_승인_요청_성공() {
        // arrange: 초기 상태 OWNER_ACCEPT, 사용자가 승인 요청(isProceed = true)
        ReflectionTestUtils.setField(mockOrder, "state", OrderState.OWNER_ACCEPT);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(mockOrder));
        UpdateOrderRequestDto dto = new UpdateOrderRequestDto(true, null);

        // act: 사용자가 처리하면 상태가 FINISH로 변경
        OrderResponseDto response = orderService.updateOrder(UserRole.USER, shopId, mockOrder.getId(), dto);

        // assert
        assertEquals(OrderState.FINISH, response.getState());
    }
}