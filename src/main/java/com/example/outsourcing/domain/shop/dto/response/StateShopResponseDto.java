package com.example.outsourcing.domain.shop.dto.response;

import com.example.outsourcing.common.enums.ShopState;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class StateShopResponseDto {
    private ShopState state;
    private LocalDateTime updateAt;
}
