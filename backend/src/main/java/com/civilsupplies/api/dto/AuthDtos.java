package com.civilsupplies.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
    ) {}

    public record LoginResponse(
        String token,
        String refreshToken,
        AdminUserDtos.AdminUserResponse user,
        String expiresAt
    ) {}

    public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
    ) {}
}
