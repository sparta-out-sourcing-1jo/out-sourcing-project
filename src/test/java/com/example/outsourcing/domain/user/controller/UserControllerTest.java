package com.example.outsourcing.domain.user.controller;

import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.user.dto.request.UserChangePasswordRequest;
import com.example.outsourcing.domain.user.dto.response.UserResponse;
import com.example.outsourcing.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockitoBean
    private JpaMetamodelMappingContext mapping;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("유저 정보를 조회(단건)")
    void getUser() throws Exception {
        //given
        long userId = 1L;
        UserResponse userResponse = new UserResponse(userId, "test@example.com", "testName", "testAddress", UserRole.USER);

        given(userService.getUser(userId)).willReturn(userResponse);

        //when & then
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testName"))
                .andExpect(jsonPath("$.address").value("testAddress"))
                .andExpect(jsonPath("$.userRole").value("USER"));
    }
}