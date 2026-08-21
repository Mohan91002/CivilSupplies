package com.civilsupplies.api.dto;

import com.civilsupplies.api.entity.QuoteStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public final class QuoteDtos {

    private QuoteDtos() {}

    public record QuoteCreateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        String email,

        String projectDetails,
        String siteLocation,
        String timeline
    ) {}

    public record QuoteResponse(
        Long id,
        String name,
        String phone,
        String email,
        String projectDetails,
        String siteLocation,
        String timeline,
        String boqFilename,
        String boqFileUrl,
        QuoteStatus status,
        LocalDateTime createdAt
    ) {}

    public record QuoteStatusUpdateRequest(
        @NotNull(message = "Status is required")
        QuoteStatus status
    ) {}
}
