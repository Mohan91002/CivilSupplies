package com.civilsupplies.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public final class AdminUserDtos {

    private AdminUserDtos() {}

    public record AdminUserResponse(
        Long id,
        String email,
        String fullName,
        List<String> roles,
        boolean active,
        LocalDateTime createdAt
    ) {}

    public record AdminUserCreateRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        String fullName,
        List<String> roles
    ) {}
}
