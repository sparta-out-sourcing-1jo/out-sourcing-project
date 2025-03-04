package com.example.outsourcing.domain.shop.dto.response;

import com.example.outsourcing.common.enums.ShopState;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StateShopResponseDto {
    private ShopState state;
    private LocalDateTime updateAt;
}
