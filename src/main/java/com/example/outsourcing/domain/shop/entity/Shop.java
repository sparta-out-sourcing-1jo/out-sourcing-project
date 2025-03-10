package com.example.outsourcing.domain.shop.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.domain.order.entity.Order;
import com.example.outsourcing.domain.shop.dto.request.ShopRequestDto;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "shops")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    private LocalTime openAt;
    private LocalTime closeAt;
    private Double averageRating = 0.0;
    private Integer reviewCount;
    private Integer minPrice;

    @Enumerated(EnumType.STRING)
    private ShopState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> shopOrders;

    // 가게 단건 수정용 생성자 메서드
    public void update(ShopRequestDto requestDto) {
        this.name = requestDto.getName();
        this.intro = requestDto.getIntro();
        this.address = requestDto.getAddress();
        this.category = requestDto.getCategory();
        this.openAt = requestDto.getOpenAt();
        this.closeAt = requestDto.getCloseAt();
        this.minPrice = requestDto.getMinPrice();
    }

    // 가게 상태 변경 생성자 메서드
    public void updateState(ShopState state) {
        this.state = state;
    }


}