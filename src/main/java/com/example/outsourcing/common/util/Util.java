package com.example.outsourcing.common.util;

import com.example.outsourcing.common.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

public class Util {
    public static void ifTrueThenThrow(boolean USER, ErrorCode errorCode) {
        if (USER) {
            throw new ResponseStatusException(errorCode.getStatus(), errorCode.getMessage());
        }
    }

    public static Pageable convertPageable(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
    }
}
