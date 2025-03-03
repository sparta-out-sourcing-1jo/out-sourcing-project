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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getUser(long userId){
        User user = userRepository.findById(userId).orElseThrow(()
        -> new InvalidRequestStateException("User not found"));
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(()->new InvalidRequestStateException("User not found"));

        if(passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestStateException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestStateException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    }
}
