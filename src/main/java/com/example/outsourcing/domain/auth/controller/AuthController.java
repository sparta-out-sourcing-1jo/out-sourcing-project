package com.example.outsourcing.domain.auth.controller;

import com.example.outsourcing.domain.auth.dto.request.LoginRequest;
import com.example.outsourcing.domain.auth.dto.request.SignupRequest;
import com.example.outsourcing.domain.auth.dto.response.LoginResponse;
import com.example.outsourcing.domain.auth.dto.response.SignupResponse;
import com.example.outsourcing.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/signup")
    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest){
        return authService.signup(signupRequest);
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}
