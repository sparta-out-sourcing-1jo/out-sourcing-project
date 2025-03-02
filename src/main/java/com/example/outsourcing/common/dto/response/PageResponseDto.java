package com.example.outsourcing.common.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageResponseDto(Page<T> entity) {
        this.content = entity.getContent();
        this.page = entity.getNumber();
        this.size = entity.getSize();
        this.totalElements = entity.getTotalElements();
        this.totalPages = entity.getTotalPages();
    }
}