package com.example.outsourcing.domain.menu.dto.Response;

import lombok.Getter;

@Getter
public class MenuResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer orderCount;

    public MenuResponseDto(Long id, String name, Integer price, Integer orderCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderCount = orderCount;
    }
}
