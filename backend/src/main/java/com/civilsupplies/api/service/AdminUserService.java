package com.civilsupplies.api.service;

import com.civilsupplies.api.common.exception.BadRequestException;
import com.civilsupplies.api.dto.AdminUserDtos.AdminUserCreateRequest;
import com.civilsupplies.api.dto.AdminUserDtos.AdminUserResponse;
import com.civilsupplies.api.entity.AdminUser;
import com.civilsupplies.api.repository.AdminUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        return adminUserRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdminUserResponse createUser(AdminUserCreateRequest request) {
        if (adminUserRepository.existsByEmail(request.email())) {
            throw new BadRequestException("User already exists with email: " + request.email());
        }

        AdminUser user = new AdminUser();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        if (request.roles() != null && !request.roles().isEmpty()) {
            user.setRoles(String.join(",", request.roles()));
        } else {
            user.setRoles("ROLE_STAFF");
        }
        user.setActive(true);

        AdminUser saved = adminUserRepository.save(user);
        return toResponse(saved);
    }

    private AdminUserResponse toResponse(AdminUser user) {
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
}
