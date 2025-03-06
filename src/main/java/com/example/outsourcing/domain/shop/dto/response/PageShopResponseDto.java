package com.example.outsourcing.domain.shop.dto.response;


import com.example.outsourcing.domain.shop.entity.Shop;
import com.example.outsourcing.domain.shop.service.ShopService;

public class PageShopResponseDto {
    private String name;
    private String address;
    private String category;
    private Double averageRating;
    private Integer minPrice;

    ShopService shopService;

    public PageShopResponseDto(Shop shop) {
        this.name = shop.getName();
        this.address = shop.getAddress();
        this.category = String.valueOf(shop.getCategory());
        this.averageRating = shopService.getAverageRating(shop.getId());
        this.minPrice = shop.getMinPrice();
    }
}
