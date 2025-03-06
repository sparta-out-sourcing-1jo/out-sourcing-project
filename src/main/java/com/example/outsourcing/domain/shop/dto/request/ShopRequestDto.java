package com.example.outsourcing.domain.shop.dto.request;

import com.example.outsourcing.common.enums.ShopCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class ShopRequestDto {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "설명을 입력해주세요.")
    private String intro;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotNull(message = "카테고리를 선택해주세요.")
    private ShopCategory category;

    private LocalTime openAt;
    private LocalTime closeAt;
    private Integer minPrice;
}
