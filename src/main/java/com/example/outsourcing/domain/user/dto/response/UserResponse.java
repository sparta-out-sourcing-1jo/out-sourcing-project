package com.example.outsourcing.domain.user.dto.response;

import com.example.outsourcing.common.enums.UserRole;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;

    public UserResponse(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
