package com.example.outsourcing.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartRequestDto {

    private final Long shopId;

    private final Long menuId;

    private final Integer quantity;
}
