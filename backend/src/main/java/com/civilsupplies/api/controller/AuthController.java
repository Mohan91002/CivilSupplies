package com.civilsupplies.api.controller;

import com.civilsupplies.api.dto.AdminUserDtos.AdminUserResponse;
import com.civilsupplies.api.dto.AuthDtos.LoginRequest;
import com.civilsupplies.api.dto.AuthDtos.LoginResponse;
import com.civilsupplies.api.dto.AuthDtos.RefreshTokenRequest;
import com.civilsupplies.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AdminUserResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        AdminUserResponse response = authService.getMe(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
}
