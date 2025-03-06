package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.common.enums.CancelReason;
import com.example.outsourcing.common.enums.OrderState;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Setter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> orderMenus = new ArrayList<>();

    @Setter
    private Integer totalPrice;


    public Order(OrderState state, User user, Cart cart) {
        this.state = state;
        this.user = user;
        this.shop = cart.getShop();
        this.totalPrice = cart.getTotalPrice();
    }

    public void updateOrderState(OrderState orderState){
        this.state = orderState;
    }

    public void updateOrderState(OrderState orderState, CancelReason reason){
        this.state = orderState;
        this.reason = reason;
    }

}
