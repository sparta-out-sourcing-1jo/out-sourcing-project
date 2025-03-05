package com.example.outsourcing.domain.order.dto.response;

import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.domain.Page;

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

    private final String menuName;

    private final Integer totalPrice;

    public OrderResponseDto(Order order) {
        this.id = order.getId();
        this.state = order.getState();
        this.userId = order.getUser().getId();
        this.userName = order.getUser().getUsername();
        this.shopId = order.getShop().getId();
        this.shopName = order.getShop().getName();
        this.menuName = order.getMenuName();
        this.totalPrice = order.getTotalPrice();
        this.reason = order.getReason();
    }

    public static OrderResponseDto toDtoOrder(Order order){
        return new OrderResponseDto(order);
    }
}
