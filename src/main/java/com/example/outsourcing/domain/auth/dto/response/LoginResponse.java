package com.example.outsourcing.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {
    private final String bearerToken;
    private final String refreshToken;

    public LoginResponse(String bearerToken, String refreshToken) {
        this.bearerToken = bearerToken;
        this.refreshToken = refreshToken;
    }
}
