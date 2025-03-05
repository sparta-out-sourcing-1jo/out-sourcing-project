package com.example.outsourcing.domain.order.dto.response;

import com.example.outsourcing.common.dto.response.PageResponseDto;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.order.entity.Cart;
import com.example.outsourcing.domain.order.entity.CartItem;
import com.example.outsourcing.domain.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
public class OrderResponseDto {

    private final Long id;

    private final OrderState state;

    // reason 이 null 이면 응답에서 reason 필드가 아예 사라짐.
    @JsonInclude(NON_NULL)
    private final CancelReason reason;

    private final Long userId;

    private final String userName;

    private final Long shopId;

    private final String shopName;

    private final PageResponseDto<CartItemResponseDto> orderMenus;

    private final Integer totalPrice;

    public OrderResponseDto(Order order, Pageable pageable) {
        this.id = order.getId();
        this.state = order.getState();
        this.reason = order.getReason();
        this.userId = order.getUser().getId();
        this.userName = order.getUser().getUsername();
        this.shopId = order.getShop().getId();
        this.shopName = order.getShop().getName();
        this.orderMenus = CartItemResponseDto.toPageCartItemDto(order.getOrderMenus(), pageable);
        this.totalPrice = order.getTotalPrice();
    }

    public static OrderResponseDto toDtoOrder(Order order, Pageable pageable){
        return new OrderResponseDto(order, pageable);
    }

    public static PageResponseDto<OrderResponseDto> toPageOrderDto(List<Order> orders, Pageable pageable){
        return new PageResponseDto<>(new PageImpl<>(
                orders.stream()
                        .map(order -> OrderResponseDto.toDtoOrder(order, pageable))
                        .toList(),
                pageable,
                orders.size()
        ));
    }
}
