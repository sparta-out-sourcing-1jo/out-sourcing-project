package com.example.outsourcing.domain.user.dto.response;

import com.example.outsourcing.common.enums.UserRole;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String username;
    private final String address;
    private final UserRole userRole;

    public UserResponse(Long id, String email, String username, String address, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.address = address;
        this.userRole = userRole;
    }
}
