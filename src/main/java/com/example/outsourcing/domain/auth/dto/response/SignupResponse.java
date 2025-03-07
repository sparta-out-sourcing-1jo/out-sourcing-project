package com.example.outsourcing.domain.auth.dto.response;

import com.example.outsourcing.common.enums.UserRole;
import lombok.Getter;

@Getter
public class SignupResponse {

    private final Long id;
    private final String email;
    private final String username;
    private final String address;
    private final UserRole userRole;

    public SignupResponse(Long id, String email, String username, String address, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.address = address;
        this.userRole = userRole;
    }
}
