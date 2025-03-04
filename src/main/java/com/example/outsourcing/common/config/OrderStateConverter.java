package com.example.outsourcing.common.config;

import com.example.outsourcing.common.enums.OrderState;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

// 컨트롤러에서 받는 인자들(RequestBody 포함)에서 OrderState 만 Convert 하는 컨버터.
public class OrderStateConverter implements Converter<String, OrderState> {

    @Override
    public OrderState convert(@Nullable String source) {
        if (source == null || source.isBlank()) {
            return null; // null 또는 빈 문자열이면 null 반환 (NPE 방지)
        }

        try {
            return OrderState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // 잘못된 값이면 null 반환 (예외 방지)
        }
    }
}
