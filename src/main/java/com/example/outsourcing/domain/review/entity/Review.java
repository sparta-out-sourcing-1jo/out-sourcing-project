package com.example.outsourcing.domain.review.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "reviews")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Integer rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @Builder
    public Review(String content, Integer rating, User user, Shop shop, Order order) {
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.shop = shop;
        this.order = order;
    }

    public void reviewUpdate(String content, Integer rating) {
        this.content = content;
        this.rating = rating;
    }

    public Review() { }
}
