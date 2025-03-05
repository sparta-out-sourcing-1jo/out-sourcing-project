package com.example.outsourcing.domain.user.service;

import com.example.outsourcing.common.config.PasswordEncoder;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.domain.user.dto.request.UserChangePasswordRequest;
import com.example.outsourcing.domain.user.dto.request.UserChangeRoleRequest;
import com.example.outsourcing.domain.user.dto.response.UserResponse;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저가 존재할 경우")
    void getUser_Success() {
        //given
        User user = new User("test@example.com", "testPassword", "testName", "testAddress", UserRole.USER);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //when
        UserResponse response = userService.getUser(1L);

        //then
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    @DisplayName("해당하는 유저가 존재하지 않을 경우")
    void getUser_UserNotFound(){
        //given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThrows(ResponseStatusException.class, () -> userService.getUser(1L));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_Success() {
        //given
        User user = new User("test@example.com", "testPassword", "testName", "testAddress", UserRole.USER);

        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword", "newPassword");

        when(userRepository.findUserById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "testPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "testPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("newTestPassword");

        //when
        userService.changePassword(1L, request);

        //then
        assertEquals("newPassword", request.getNewPassword());
    }

    @Test
    @DisplayName("역활 변경 성공")
    void changeUserRole_Success() {
        //given
        User user = new User("test@example.com", "testPassword", "testName", "testAddress", UserRole.USER);

        UserChangeRoleRequest request = new UserChangeRoleRequest("USER", "OWNER");

        when(userRepository.findUserByIdOrElseThrow(any())).thenReturn(user);

        //when
        userService.changeUserRole(1L, request);

        //then
        assertEquals(UserRole.OWNER, user.getRole());
    }

    @Test
    @DisplayName("바꾸는 역활이 기존과 같을 경우 생기는 오류")
    void changeUserRole_SameRole(){
        //given
        User user = new User("test@example.com", "testPassword", "testName", "testAddress", UserRole.USER);

        UserChangeRoleRequest request = new UserChangeRoleRequest("USER", "USER");

        when(userRepository.findUserByIdOrElseThrow(any())).thenReturn(user);

        //when & then
        assertThrows(ResponseStatusException.class, () -> userService.changeUserRole(1L, request));
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUser_Success() {
        //given
        User user = new User("test@example.com", "testPassword", "testName", "testAddress", UserRole.USER);

        when(userRepository.findUserByIdOrElseThrow(any())).thenReturn(user);

        //when
        userService.deleteUser(1L);

        //then
        assertNotNull(user.getDeletedAt());
    }
}