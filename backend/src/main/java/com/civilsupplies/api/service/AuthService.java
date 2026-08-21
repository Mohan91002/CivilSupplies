package com.civilsupplies.api.service;

import com.civilsupplies.api.common.exception.BadRequestException;
import com.civilsupplies.api.common.exception.ResourceNotFoundException;
import com.civilsupplies.api.dto.AdminUserDtos.AdminUserResponse;
import com.civilsupplies.api.dto.AuthDtos.LoginRequest;
import com.civilsupplies.api.dto.AuthDtos.LoginResponse;
import com.civilsupplies.api.dto.AuthDtos.RefreshTokenRequest;
import com.civilsupplies.api.entity.AdminUser;
import com.civilsupplies.api.repository.AdminUserRepository;
import com.civilsupplies.api.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AdminUserRepository adminUserRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       AdminUserRepository adminUserRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.adminUserRepository = adminUserRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.email());

        AdminUser adminUser = adminUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));

        long expiryTimestamp = System.currentTimeMillis() + tokenProvider.getAccessTokenExpiryMs();
        String expiresAt = Instant.ofEpochMilli(expiryTimestamp).toString();

        return new LoginResponse(accessToken, refreshToken, toUserResponse(adminUser), expiresAt);
    }

    @Transactional(readOnly = true)
    public LoginResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getUsernameFromToken(refreshToken);
        AdminUser adminUser = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (!adminUser.isActive()) {
            throw new BadRequestException("User account is inactive");
        }

        CustomUserDetails userDetails = new CustomUserDetails(adminUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(email);

        long expiryTimestamp = System.currentTimeMillis() + tokenProvider.getAccessTokenExpiryMs();
        String expiresAt = Instant.ofEpochMilli(expiryTimestamp).toString();

        return new LoginResponse(newAccessToken, newRefreshToken, toUserResponse(adminUser), expiresAt);
    }

    @Transactional(readOnly = true)
    public AdminUserResponse getMe(String email) {
        AdminUser adminUser = adminUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return toUserResponse(adminUser);
    }

    private AdminUserResponse toUserResponse(AdminUser user) {
        List<String> rolesList = Arrays.stream(user.getRoles().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                rolesList,
                user.isActive(),
                user.getCreatedAt()
        );
    }

    // Helper inner UserDetails wrapper for token refresh
    private static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        public CustomUserDetails(AdminUser user) {
            super(user.getEmail(), user.getPasswordHash(),
                    Arrays.stream(user.getRoles().split(","))
                            .map(String::trim)
                            .filter(r -> !r.isEmpty())
                            .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                            .toList());
        }
    }
}
