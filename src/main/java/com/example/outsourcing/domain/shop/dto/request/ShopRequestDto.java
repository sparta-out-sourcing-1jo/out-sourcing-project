package com.example.outsourcing.domain.shop.dto.request;

import com.example.outsourcing.common.enums.ShopCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class ShopRequestDto {
    private String name;
    private String intro;
    private String address;
    private ShopCategory category;
    private LocalTime openAt;
    private LocalTime closeAt;
    private Double minPrice;
}
