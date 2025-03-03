package com.example.outsourcing.domain.shop.dto.response;

import com.example.outsourcing.common.enums.ShopState;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ShopResponseDto {

    private Long id;
    private String name;
    private String intro;
    private String address;
    private String category;
    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private Double averageRating;
    private Integer reviewCount;
    private Double minPrice;
    private ShopState state;
    private String owner;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
