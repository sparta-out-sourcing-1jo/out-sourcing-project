package com.example.outsourcing.domain.shop.dto.response;


import com.example.outsourcing.domain.shop.entity.Shop;

public class PageShopResponseDto {
    private String name;
    private String address;
    private String category;
    private Double averageRating;
    private Double minPrice;

    public PageShopResponseDto(Shop shop) {
        this.name = shop.getName();
        this.address = shop.getAddress();
        this.category = String.valueOf(shop.getCategory());
        this.averageRating = shop.getAverageRating();
        this.minPrice = shop.getMinPrice();
    }
}
