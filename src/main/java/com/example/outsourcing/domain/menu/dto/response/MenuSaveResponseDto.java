package com.example.outsourcing.domain.menu.dto.response;

import lombok.Getter;

@Getter
public class MenuSaveResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;


    public MenuSaveResponseDto(Long id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
