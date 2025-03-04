package com.example.outsourcing.domain.order.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.common.enums.OrderState;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private Long userId;
    private String userName;

    private Long shopId;
    private String shopName;

    private String menuName;
    private Double totalPrice;
}