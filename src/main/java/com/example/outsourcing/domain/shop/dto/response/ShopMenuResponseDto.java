package com.example.outsourcing.domain.shop.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopMenuResponseDto {
    // 상점 단건 조회 시, 상점 정보 + 메뉴 리스트 합쳐서 응답할 dto
    private ShopResponseDto shopInfo; // 상점 정보
    private List<MenuResponseDto> menus; // 메뉴 정보 (일단은 주석처리)
}
