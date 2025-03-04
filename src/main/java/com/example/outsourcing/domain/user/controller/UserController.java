package com.example.outsourcing.domain.user.controller;

import com.example.outsourcing.domain.auth.annotation.Auth;
import com.example.outsourcing.domain.auth.dto.AuthUser;
import com.example.outsourcing.domain.user.dto.request.UserChangePasswordRequest;
import com.example.outsourcing.domain.user.dto.request.UserChangeRoleRequest;
import com.example.outsourcing.domain.user.dto.response.UserResponse;
import com.example.outsourcing.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId){
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@Auth AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest){
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PutMapping("/users")
    public void changeUserRole(@Auth AuthUser authUser, @RequestBody UserChangeRoleRequest userChangeRoleRequest){
        userService.changeUserRole(authUser.getId(), userChangeRoleRequest);
    }

    @DeleteMapping("/users")
    public void deleteUser(@Auth AuthUser authUser){
        userService.deleteUser(authUser.getId());
    }
}
