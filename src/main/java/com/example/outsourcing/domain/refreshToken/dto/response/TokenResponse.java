package com.example.outsourcing.domain.refreshToken.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String bearerToken;
    private String refreshToken;
}
