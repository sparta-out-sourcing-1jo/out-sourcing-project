package com.example.outsourcing.domain.user.service;

import com.example.outsourcing.common.config.PasswordEncoder;
import com.example.outsourcing.domain.user.dto.request.UserChangePasswordRequest;
import com.example.outsourcing.domain.user.dto.response.UserResponse;
import com.example.outsourcing.domain.user.entity.User;
import com.example.outsourcing.domain.user.repository.UserRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static com.example.outsourcing.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId){
        User user = userRepository.findUserById(userId)
                .orElseThrow(
                () -> new ResponseStatusException(
                        USER_NOT_FOUND.getStatus(),
                        USER_NOT_FOUND.getMessage()
                )
        );
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                USER_NOT_FOUND.getStatus(),
                                USER_NOT_FOUND.getMessage()
                        )
                );

        if(passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(PASSWORD_SAME_AS_OLD.getStatus(), PASSWORD_SAME_AS_OLD.getMessage());
        }

        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
        }

        user.changePassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    }
}
