package com.civilsupplies.api.controller;

import com.civilsupplies.api.dto.AdminUserDtos.AdminUserCreateRequest;
import com.civilsupplies.api.dto.AdminUserDtos.AdminUserResponse;
import com.civilsupplies.api.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> listUsers() {
        List<AdminUserResponse> users = adminUserService.listUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody AdminUserCreateRequest request) {
        AdminUserResponse response = adminUserService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
