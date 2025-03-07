package com.example.outsourcing.domain.menu.entity;

import com.example.outsourcing.common.entity.BaseTimeEntity;
import com.example.outsourcing.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.outsourcing.domain.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "menus")
@NoArgsConstructor
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

    public Menu(String name, Integer price, Shop shop) {
        this.name = name;
        this.price = price;
        this.shop = shop;
        this.orderCount = 0;
    }

    public void updateMenu(MenuUpdateRequestDto menuUpdateRequestDto) {
        this.name = menuUpdateRequestDto.getName();
        this.price = menuUpdateRequestDto.getPrice();
    }

    public void setDeletedAt() {
        super.setDeletedAt();;
    }

    public void increaseOrderCount(Integer quantity) {
        this.orderCount += quantity;
    }
}