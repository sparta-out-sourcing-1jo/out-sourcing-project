package com.example.outsourcing.domain.shop.dto.request;

import com.example.outsourcing.common.enums.ShopCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AddShopRequestDto {
    private String name;
    private String intro;
    private String address;
    private ShopCategory category;
    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private Double minPrice;
}
