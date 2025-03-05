package com.example.outsourcing.domain.menu.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.domain.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "menus")
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;
    private Integer orderCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public void increaseOrderCount(int quantity) {
        this.orderCount = this.orderCount + quantity;
    }
}
