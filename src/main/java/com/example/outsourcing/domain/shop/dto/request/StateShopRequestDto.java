package com.example.outsourcing.domain.shop.dto.request;

import com.example.outsourcing.common.enums.ShopState;
import lombok.Getter;

@Getter
public class StateShopRequestDto {
    private ShopState state;
}
