package com.example.outsourcing.domain.user.controller;

import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.user.dto.request.UserChangePasswordRequest;
import com.example.outsourcing.domain.user.dto.request.UserChangeRoleRequest;
import com.example.outsourcing.domain.user.dto.response.UserResponse;
import com.example.outsourcing.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/profile")
    public ResponseEntity<UserResponse> getUser(@Auth AuthUser authUser) {
        return ResponseEntity.ok(userService.getUser(authUser.getId()));
    }

    @PutMapping("/users/password")
    public ResponseEntity<String> changePassword(
            @Auth AuthUser authUser,
            @RequestBody UserChangePasswordRequest userChangePasswordRequest
    ) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);

        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }

    @PutMapping("/users/role")
    public ResponseEntity<String> changeUserRole(@Auth AuthUser authUser, @RequestBody UserChangeRoleRequest userChangeRoleRequest){
        userService.changeUserRole(authUser.getId(), userChangeRoleRequest);

        return ResponseEntity.ok("역활 변경이 완료되었습니다.");
    }

    @DeleteMapping("/users/delete")
    public ResponseEntity<String> deleteUser(@Auth AuthUser authUser){
        userService.deleteUser(authUser.getId());

        return ResponseEntity.ok("유저가 정상적으로 삭제되었습니다.");
    }
}
