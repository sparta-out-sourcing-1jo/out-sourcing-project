package com.example.outsourcing.domain.shop.dto.response;

import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.domain.shop.entity.Shop;
import lombok.Getter;

@Getter
public class PageShopResponseDto {
    private String name;
    private String address;
    private ShopCategory category;
    private Double averageRating;
    private Integer minPrice;

    public PageShopResponseDto(Shop shop, Double averageRating) {
        this.name = shop.getName();
        this.address = shop.getAddress();
        this.category = shop.getCategory();
        this.averageRating = averageRating;
        this.minPrice = shop.getMinPrice();
    }
}