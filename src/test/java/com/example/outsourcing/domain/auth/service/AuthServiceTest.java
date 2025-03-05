package com.example.outsourcing.domain.auth.service;

import com.example.outsourcing.common.config.PasswordEncoder;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.common.util.JwtUtil;
import com.example.outsourcing.domain.auth.dto.request.LoginRequest;
import com.example.outsourcing.domain.auth.dto.request.SignupRequest;
import com.example.outsourcing.domain.auth.dto.response.LoginResponse;
import com.example.outsourcing.domain.auth.dto.response.SignupResponse;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import static com.example.outsourcing.common.exception.ErrorCode.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_Success(){
        //given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "testPassword", "testname", "testAddress", "USER");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    ReflectionTestUtils.setField(user, "id", 1L);
                    return user;
                });
        when(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class)))
                .thenReturn("mockJwtToken");

        //when
        SignupResponse signupResponse = authService.signup(signupRequest);

        //then
        assertNotNull(signupResponse);
        assertEquals("mockJwtToken", signupResponse.getBearerToken());

        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(passwordEncoder).encode(signupRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).createToken(anyLong(), anyString(), any(UserRole.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_Fail_EmailDuplication(){
        //given
        SignupRequest request = new SignupRequest("test@example.com", "testPassword", "testname", "testAddress", "USER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        //when & then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()->authService.signup(request));
        assertEquals(USER_EMAIL_DUPLICATION.getStatus(), exception.getStatusCode());

        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success(){
        //given
        LoginRequest request = new LoginRequest("test@example.com", "testPassword");
        User user = new User("test@example.com", "encodedPassword", "testName", "testAddress", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findUserByEmailAndDeletedAtIsNull(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole())).thenReturn("mockJwtToken");

        //when
        LoginResponse response = authService.login(request);

        //then
        assertNotNull(response);
        assertEquals("mockJwtToken", response.getBearerToken());

        verify(userRepository).findUserByEmailAndDeletedAtIsNull(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil).createToken(user.getId(), user.getEmail(), user.getRole());
    }
}