package com.example.outsourcing.domain.shop.dto.request;

import com.example.outsourcing.common.enums.ShopState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StateShopRequestDto {
    
    @NotNull(message = "영업 상태를 선택해주세요")
    private ShopState state;
}
