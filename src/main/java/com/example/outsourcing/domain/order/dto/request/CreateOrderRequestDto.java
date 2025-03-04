package com.example.outsourcing.domain.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderRequestDto {

    private final Long menuId;

    private final Long shopId;
}
