package com.example.outsourcing.domain.shop.dto.response;

import com.example.outsourcing.common.enums.ShopCategory;
import com.example.outsourcing.common.enums.ShopState;
import com.example.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
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
    private ShopCategory category;
    private LocalDateTime openAt;
    private LocalDateTime closeAt;
    private Double averageRating;
    private Integer reviewCount;
    private Double minPrice;
    private ShopState state;
}
