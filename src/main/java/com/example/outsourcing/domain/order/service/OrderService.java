package com.example.outsourcing.domain.order.service;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.menu.repository.MenuRepository;
import com.example.outsourcing.domain.order.dto.request.UpdateOrderRequestDto;
import com.example.outsourcing.domain.order.dto.response.OrderResponseDto;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.order.repository.CartItemRepository;
import com.example.outsourcing.domain.order.repository.CartRepository;
import com.example.outsourcing.domain.order.repository.OrderRepository;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.repository.ShopRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.example.outsourcing.common.exception.ErrorCode.*;
import static com.example.outsourcing.common.util.Util.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final CartRepository cartRepository;

    /**
     * 1. 유저가 아니면 주문 생성 못함.
     * 2. 최소주문금액을 넘지 못하면 주문 생성 못함.
     * 3. 가게 문 닫으면 주문 생성 못함.
     */
    @Transactional
    public OrderResponseDto createOrder(AuthUser authUser, Pageable pageable){
        ifTrueThenThrow(
                !UserRole.USER.equals(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        User user = userRepository.findUserById(authUser.getId()).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

        Cart cart = cartRepository.findCartByUserAndDeletedAtIsNull(user);

        ifTrueThenThrow(
                ShopState.CLOSE.equals(cart.getShop().getState()),
                OVER_TIME_TO_OPEN
        );

        ifTrueThenThrow(
                cart.getShop().getMinPrice() > cart.getTotalPrice(),
                INVALID_PRICE
        );

        // 새로운 Order 생성
        Order newOrder = new Order(OrderState.CLIENT_ACCEPT, user, cart);
        newOrder.setTotalPrice(cart.getTotalPrice()); // totalPrice 설정

        // CartItem 복사 및 Order에 연결
        List<CartItem> orderMenus = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            CartItem newOrderItem = new CartItem(cartItem.getMenu(), cartItem.getQuantity()); // 새로운 CartItem 생성 (복사본)
            newOrderItem.updateOrder(newOrder); // Order 연결
            orderMenus.add(newOrderItem);
        }

        newOrder.setOrderMenus(orderMenus); // Order에 새로운 CartItem 목록 설정
        Order order = orderRepository.save(newOrder); // Order 저장 (CascadeType.ALL 로 인해 CartItem들도 함께 저장됨)
        cart.setDeletedAt();
        cartRepository.save(cart); // Cart soft delete (deletedAt 업데이트)

        return new OrderResponseDto(order, pageable);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findOrder(AuthUser authUser, Long orderId, Pageable pageable) {
        ifTrueThenThrow (
                !EnumSet.of(UserRole.USER, UserRole.OWNER).contains(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(ORDER_NOT_FOUND.getStatus(), ORDER_NOT_FOUND.getMessage()));

        ifTrueThenThrow (
                !order.getUser().getId().equals(authUser.getId())
                && !order.getShop().getId().equals(authUser.getId()),
                USER_ACCESS_DENIED
        );

        return new OrderResponseDto(order, pageable);
    }

    /**
     * 1. 유저 혹은 사장이 아니면 조회 못함
     * 2. 그 외에는 기본적으로 빈 페이지라도 응답함.
     * 3. userRole, orderState 에 따라 조회가 다르게 실행됨.
     *  3-1. 유저는 본인꺼만 검색가능, 사장은 본인 가게꺼만 검색가능
     *  3-2. (사장전용) 지정한 가게가 있으면 가게를 포함하여 주문검색
     *  3-3. 지정한 주문상태가 있으면 주문상태를 포함하여 주문검색
     */
    @Transactional(readOnly = true)
    public PageResponseDto<OrderResponseDto> findOrders(AuthUser authUser, OrderState orderState, Long shopId, Pageable pageable) {
        ifTrueThenThrow (
                !EnumSet.of(UserRole.USER, UserRole.OWNER).contains(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        User user = userRepository.findUserById(authUser.getId()).orElseThrow(
                () -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

        List<Order> orders = Collections.emptyList();
        if (UserRole.USER.equals(authUser.getUserRole())) {
            // 고객: 자신의 주문만 조회 (주문 상태 필터 적용)
            orders = (orderState != null)
                    ? orderRepository.findOrdersByUserAndState(user, orderState)
                    : orderRepository.findOrdersByUser(user);
        } else { // Owner인 경우
            if (shopId != null) {
                // 특정 가게가 지정된 경우
                Shop shop = shopRepository.findShopById(shopId)
                        .orElseThrow(() -> new ResponseStatusException(SHOP_NOT_FOUND.getStatus(), SHOP_NOT_FOUND.getMessage()));
                orders = (orderState != null)
                        ? orderRepository.findOrdersByShopAndState(shop, orderState)
                        : orderRepository.findOrdersByShop(shop);
            } else {
                // 가게가 지정되지 않은 경우, 해당 Owner 가 소유한 모든 가게의 주문 조회
                List<Shop> shops = shopRepository.findShopsByUserAndDeletedAtIsNull(user);
                orders = (orderState != null)
                        ? orderRepository.findOrdersByStateAndShopIn(orderState, shops)
                        : orderRepository.findOrdersByShopIn(shops);
            }
        }
        return OrderResponseDto.toPageOrderDto(orders, pageable);
    }

    /**
     * 1. 유저 혹은 사장이 아니면 갱신 못함
     * 2. 주문상태가 이미 FINISH 거나, 삭제된 주문이거나, 선택한 주문과 가게가 다르다면 갱신 못함
     * 3. 이전 주문상태, 유저 역할, 갱신 의사에 따라 적용되는 로직이 다름 (유저와 사장이 티키타카)
     *  3-1. 주문을 계속 진행한다면 주문 상태만 갱신되며, 해당 주문은 FINISH 에 가까워지게됨.
     *  3-2. 중간에 갱신 의사가 없다면, deletedAt 세팅
     */
    @Transactional
    public OrderResponseDto updateOrder(AuthUser authUser, Long shopId, Long orderId, UpdateOrderRequestDto dto, Pageable pageable) {
        ifTrueThenThrow(
                !EnumSet.of(UserRole.USER, UserRole.OWNER).contains(authUser.getUserRole()),
                USER_ACCESS_DENIED
        );

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResponseStatusException(ORDER_NOT_FOUND.getStatus(), ORDER_NOT_FOUND.getMessage()));

        ifTrueThenThrow(
                OrderState.FINISH.equals(order.getState())         // 주문 상태가 Finish 이거나
                || order.getDeletedAt() != null                          // 주문이 소프트딜리트 당한 적이 없거나
                || !order.getShop().getId().equals(shopId),              // 주문의 가게Id 와 요청한 가게Id가 다르면
                USER_ACCESS_DENIED                                       // 인가 예외 발생
        );

        if ( // 1. 주문 생성 이후 사장이 주문수락
                OrderState.CLIENT_ACCEPT.equals(order.getState()) 
                && UserRole.OWNER.equals(authUser.getUserRole()) 
                && dto.getIsProceed()
        ) {
            order.updateOrderState(OrderState.OWNER_ACCEPT);
        }
        if ( // 2. 주문 생성 이후 고객이 주문취소 (주문 끝)
                OrderState.CLIENT_ACCEPT.equals(order.getState())
                        && UserRole.USER.equals(authUser.getUserRole())
                        && !dto.getIsProceed()
        ) {
            order.updateOrderState(OrderState.CLIENT_CANCEL, dto.getReason());
            order.setDeletedAt(); // 주문 소프트 딜리트
        }

        if ( // 3. 사장 주문수락 이후 고객이 최종수락 (주문 끝)
                OrderState.OWNER_ACCEPT.equals(order.getState())
                        && UserRole.USER.equals(authUser.getUserRole())
                        && dto.getIsProceed()
        ) {
            order.updateOrderState(OrderState.FINISH);
            order.getOrderMenus().forEach(cartItem -> cartItem.getMenu().increaseOrderCount(cartItem.getQuantity())); // 메뉴의 주문 수 증가
        }
        if ( // 4. 사장 주문수락 이후 사장이 주문취소 (주문 끝)
                OrderState.OWNER_ACCEPT.equals(order.getState())
                        && UserRole.OWNER.equals(authUser.getUserRole())
                        && !dto.getIsProceed()
        ) {
            order.updateOrderState(OrderState.OWNER_CANCEL, dto.getReason());
            order.setDeletedAt(); // 주문 소프트 딜리트
        }

        return new OrderResponseDto(order, pageable);
    }


}
