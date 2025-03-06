package com.example.outsourcing.domain.auth.service;

import com.example.outsourcing.common.config.PasswordEncoder;
import com.example.outsourcing.common.enums.UserRole;
import com.example.outsourcing.common.util.JwtUtil;
import com.example.outsourcing.domain.auth.dto.request.LoginRequest;
import com.example.outsourcing.domain.auth.dto.request.SignupRequest;
import com.example.outsourcing.domain.auth.dto.response.LoginResponse;
import com.example.outsourcing.domain.auth.dto.response.SignupResponse;
import com.example.outsourcing.domain.refreshToken.dto.response.TokenResponse;
import com.example.outsourcing.domain.refreshToken.entity.RefreshToken;
import com.example.outsourcing.domain.refreshToken.repository.RefreshTokenRepository;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.outsourcing.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResponseStatusException(USER_EMAIL_DUPLICATION.getStatus(), USER_EMAIL_DUPLICATION.getMessage());
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
                signupRequest.getEmail(),
                encodedPassword,
                signupRequest.getUserName(),
                signupRequest.getAddress(),
                userRole
        );
        userRepository.save(newUser);

        return new SignupResponse(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getUsername(),
                newUser.getAddress(),
                newUser.getRole()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findUserByEmailAndDeletedAtIsNull(loginRequest.getEmail()).orElseThrow(
                ()->new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken();

        //기존 Refresh토큰 삭제 후 새로 저장
//        refreshTokenRepository.findByUserId(user.getId()).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));

        return new LoginResponse(bearerToken, refreshToken);
    }

    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken){
        if(!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ResponseStatusException(INVALID_TOKEN.getStatus(), INVALID_TOKEN.getMessage());
        }

        RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new ResponseStatusException(TOKEN_NOT_FOUND.getStatus(), TOKEN_NOT_FOUND.getMessage()));

        User user = userRepository.findUserByIdOrElseThrow(storedToken.getUserId());

        //새로운 bearer토큰 발급
        String newToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getRole());

        return new TokenResponse(newToken, refreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
