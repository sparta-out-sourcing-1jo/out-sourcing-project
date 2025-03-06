package com.example.outsourcing.domain.menu.dto.response;

import com.example.outsourcing.domain.menu.entity.Menu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer orderCount;

    public static MenuResponseDto of (Menu menu) {
        return new MenuResponseDto(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getOrderCount()
        );
    }
}
