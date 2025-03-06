package com.example.outsourcing.domain.menu.dto;

import lombok.Getter;

@Getter
public class MenuSaveResponseDto {

    private final Long id;
    private final String name;
    private final Double price;


    public MenuSaveResponseDto(Long id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
