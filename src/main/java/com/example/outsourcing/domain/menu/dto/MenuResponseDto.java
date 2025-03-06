package com.example.outsourcing.domain.menu.dto;

import lombok.Getter;

@Getter
public class MenuResponseDto {

    private final Long id;
    private final String name;
    private final Double price;
    private final Integer orderCount;

    public MenuResponseDto(Long id, String name, Double price, Integer orderCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderCount = orderCount;
    }
}
