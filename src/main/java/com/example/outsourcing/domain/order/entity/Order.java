package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.menu.entity.Menu;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주문상태
    @Enumerated(EnumType.STRING)
    private OrderState state;

    // 주문취소이유
    @Enumerated(EnumType.STRING)
    private CancelReason reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String userName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    private String shopName;

    private String menuName;

    private Double totalPrice;

    public Order(OrderState state, User user, Shop shop, Menu menu) {
        this.state = state;
        this.user = user;
        this.userName = user.getUsername();
        this.shop = shop;
        this.shopName = shop.getName();
        this.menuName = menu.getName();
        this.totalPrice = menu.getPrice();
    }

    public void updateOrderState(OrderState orderState){
        this.state = orderState;
    }

    public void updateOrderState(OrderState orderState, CancelReason reason){
        this.state = orderState;
        this.reason = reason;
    }
}
