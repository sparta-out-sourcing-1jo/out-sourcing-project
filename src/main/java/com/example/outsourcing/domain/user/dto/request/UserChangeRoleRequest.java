package com.example.outsourcing.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangeRoleRequest {
    @NotBlank
    private String prUserRole;

    @NotBlank
    private String newUserRole;
}
