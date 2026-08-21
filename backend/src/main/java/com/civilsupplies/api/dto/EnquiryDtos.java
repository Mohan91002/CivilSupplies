package com.civilsupplies.api.dto;

import com.civilsupplies.api.entity.EnquiryStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public final class EnquiryDtos {

    private EnquiryDtos() {}

    public record EnquiryCreateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,

        String city,
        String projectType,
        List<String> materials,
        String quantity,
        String message
    ) {}

    public record EnquiryResponse(
        Long id,
        String name,
        String phone,
        String email,
        String city,
        String projectType,
        List<String> materials,
        String quantity,
        String message,
        EnquiryStatus status,
        LocalDateTime createdAt
    ) {}

    public record EnquiryStatusUpdateRequest(
        @NotNull(message = "Status is required")
        EnquiryStatus status
    ) {}
}
