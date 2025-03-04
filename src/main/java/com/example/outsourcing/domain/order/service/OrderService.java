package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.ShopState;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.example.outsourcing.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;

    /**
     * 1. 유저가 아니면 주문 생성 못함.
     * 2. 최소주문금액을 넘지 못하면 주문 생성 못함.
     * 3. 가게 문 닫으면 주문 생성 못함.
     */
    @Transactional
    public OrderResponseDto createOrder(Long userId, UserRole userRole, CreateOrderRequestDto dto){
        if (!UserRole.USER.equals(userRole)) {
            throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
        }

        User user = userRepository.findUserById(userId).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
        Shop shop = shopRepository.findShopById(dto.getShopId()).orElseThrow(
                () -> new ResponseStatusException(SHOP_NOT_FOUND.getStatus(), SHOP_NOT_FOUND.getMessage()));
        Menu menu = menuRepository.findMenuById(dto.getMenuId()).orElseThrow(
                () -> new ResponseStatusException(MENU_NOT_FOUND.getStatus(), MENU_NOT_FOUND.getMessage()));

        if (shop.getMinPrice() > menu.getPrice()) {
            throw new ResponseStatusException(INVALID_PRICE.getStatus(), INVALID_PRICE.getMessage());
        }
        if (ShopState.CLOSE.equals(shop.getState())) {
            throw new ResponseStatusException(OVER_TIME_TO_OPEN.getStatus(), OVER_TIME_TO_OPEN.getMessage());
        }

        Order order = new Order(OrderState.CLIENT_ACCEPT, user, shop, menu);
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    /**
     * 1. 유저 혹은 사장이 아니면 조회 못함
     * 2. 조회하는 본인이 주문 생성자도 아니고 가게사장도 아니면 조회 못함
     */
    @Transactional(readOnly = true)
    public OrderResponseDto findOrder(Long userId, UserRole userRole, Long orderId){
        if (!EnumSet.of(UserRole.USER, UserRole.OWNER).contains(userRole)) {
            throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
        }

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(ORDER_NOT_FOUND.getStatus(), ORDER_NOT_FOUND.getMessage()));

        if (!Objects.equals(order.getUser().getId(), userId) && !Objects.equals(order.getShop().getUser().getId(), userId)) {
            throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
        }

        return new OrderResponseDto(order);
    }

    /**
     * 1. 유저 혹은 사장이 아니면 조회 못함
     * 2. 그 외에는 기본적으로 빈 페이지라도 응답함.
     * 3. userRole,shopId, orderState 에 따라 조회가 다르게 실행됨.
     *  3-1. 유저는 본인꺼만 검색가능, 사장은 본인 가게꺼만 검색가능
     *  3-2. 지정한 가게가 있으면 가게를 포함하여 주문검색
     *  3-3. 지정한 주문상태가 있으면 주문상태를 포함하여 주문검색
     */
    @Transactional(readOnly = true)
    public PageResponseDto<OrderResponseDto> findOrders(Long userId, UserRole userRole, Long shopId, OrderState orderState, Pageable pageable) {
        if (!EnumSet.of(UserRole.USER, UserRole.OWNER).contains(userRole)) {
            throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
        }

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

        Page<Order> orders = UserRole.USER.equals(userRole)
                ? getOrdersForUser(user, shopId, orderState, pageable)
                : getOrdersForOwner(user, shopId, orderState, pageable);

        return new PageResponseDto<>(orders.map(OrderResponseDto::toDtoOrder));
    }

    /**
     * 1. 유저 혹은 사장이 아니면 갱신 못함
     * 2. 주문상태가 이미 FINISH 거나, 삭제된 주문이거나, 선택한 주문과 가게가 다르다면 갱신 못함
     * 3. 이전 주문상태, 유저 역할, 갱신 의사에 따라 적용되는 로직이 다름 (유저와 사장이 티키타카)
     *  3-1. 주문을 계속 진행한다면 주문 상태만 갱신되며, 해당 주문은 FINISH 에 가까워지게됨.
     *  3-2. 중간에 갱신 의사가 없다면, deletedAt 세팅
     */
    @Transactional
    public OrderResponseDto updateOrder(UserRole userRole, Long shopId, Long orderId, UpdateOrderRequestDto dto) {
        if (!EnumSet.of(UserRole.USER, UserRole.OWNER).contains(userRole)) {
            throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
        }

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(ORDER_NOT_FOUND.getStatus(), ORDER_NOT_FOUND.getMessage()));

        if (
                OrderState.FINISH.equals(order.getState())
                || order.getDeletedAt() != null
                || !order.getShop().getId().equals(shopId)
        ) {
            throw new ResponseStatusException(USER_ACCESS_DENIED.getStatus(), USER_ACCESS_DENIED.getMessage());
        }

        if (OrderState.CLIENT_ACCEPT.equals(order.getState()) && UserRole.USER.equals(userRole) && !dto.getIsProceed()) {
            order.updateOrderState(OrderState.CLIENT_CANCEL, dto.getReason());
            order.setDeletedAt();
        }
        if (OrderState.OWNER_ACCEPT.equals(order.getState()) && UserRole.OWNER.equals(userRole) && !dto.getIsProceed()) {
            order.updateOrderState(OrderState.OWNER_CANCEL, dto.getReason());
            order.setDeletedAt();
        }

        if (OrderState.CLIENT_ACCEPT.equals(order.getState()) && UserRole.OWNER.equals(userRole) && dto.getIsProceed()) {
            order.updateOrderState(OrderState.OWNER_ACCEPT);
        }
        if (OrderState.OWNER_ACCEPT.equals(order.getState()) && UserRole.USER.equals(userRole) && dto.getIsProceed()) {
            order.updateOrderState(OrderState.FINISH);
        }

        return new OrderResponseDto(order);
    }


    // 일반 사용자 주문 조회 메서드
    private Page<Order> getOrdersForUser(User user, Long shopId, OrderState orderState, Pageable pageable) {
        return Optional.ofNullable(shopId)
                .map(id -> {
                    Shop shop = shopRepository.findShopById(id)
                            .orElseThrow(() -> new ResponseStatusException(SHOP_NOT_FOUND.getStatus(), SHOP_NOT_FOUND.getMessage()));
                    return (orderState == null)
                            ? orderRepository.findOrdersByUserAndShop(user, shop, pageable)
                            : orderRepository.findOrdersByUserAndShopAndState(user, shop, orderState, pageable);
                })
                .orElseGet(() ->
                        (orderState == null)
                                ? orderRepository.findOrdersByUser(user, pageable)
                                : orderRepository.findOrdersByUserAndState(user, orderState, pageable)
                );
    }

    // 가게 사장 주문 조회 메서드
    private Page<Order> getOrdersForOwner(User user, Long shopId, OrderState orderState, Pageable pageable) {
        return Optional.ofNullable(shopId)
                .map(id -> {
                    Shop shop = shopRepository.findShopById(id)
                            .orElseThrow(() -> new ResponseStatusException(SHOP_NOT_FOUND.getStatus(), SHOP_NOT_FOUND.getMessage()));
                    return (orderState == null)
                            ? orderRepository.findOrdersByShop(shop, pageable)
                            : orderRepository.findOrdersByShopAndState(shop, orderState, pageable);
                })
                .orElseGet(() -> {
                    List<Shop> shops = shopRepository.findShopsByDeletedAtIsNullAndUser(user, pageable);
                    return (orderState == null)
                            ? orderRepository.findOrdersByShopIn(shops, pageable)
                            : orderRepository.findOrdersByShopInAndState(shops, orderState, pageable);
                });
    }
}
