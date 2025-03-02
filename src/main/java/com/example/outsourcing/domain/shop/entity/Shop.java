package com.example.outsourcing.domain.shop.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "shops")
public class Shop extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String intro;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private Double averageRating;
    private Integer reviewCount;
    private Double minPrice;

    @Enumerated(EnumType.STRING)
    private ShopState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}