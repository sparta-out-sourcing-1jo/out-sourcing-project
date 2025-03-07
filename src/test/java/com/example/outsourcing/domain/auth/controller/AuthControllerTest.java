package com.example.outsourcing.domain.auth.controller;

import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.auth.dto.request.LoginRequest;
import com.example.outsourcing.domain.auth.dto.request.SignupRequest;
import com.example.outsourcing.domain.auth.dto.response.LoginResponse;
import com.example.outsourcing.domain.auth.dto.response.SignupResponse;
import com.example.outsourcing.domain.auth.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @MockitoBean
    private JpaMetamodelMappingContext mapping;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_Success() throws Exception {
        //given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "Testpassword123*", "testName", "testAddress", "USER");
        SignupResponse signupResponse = new SignupResponse(1L, "test@example.com", "testName", "testAddress", UserRole.USER);

        given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);

        //when & then
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON) // 요청 본문이 Json
                .content(objectMapper.writeValueAsString(signupRequest))) // 객체를 Json 문자열로 반환하여 전송
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testName"))
                .andExpect(jsonPath("$.address").value("testAddress"))
                .andExpect(jsonPath("$.userRole").value("USER"));

        verify(authService, times(1)).signup(any(SignupRequest.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Testpassword123!");
        LoginResponse loginResponse = new LoginResponse("jwt-token", "refreshToken");

        given(authService.login(any(LoginRequest.class))).willReturn(loginResponse);

        //when & then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

        verify(authService, times(1)).login(any(LoginRequest.class));
    }
}