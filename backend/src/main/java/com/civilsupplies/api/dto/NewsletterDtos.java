package com.civilsupplies.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class NewsletterDtos {

    private NewsletterDtos() {}

    public record NewsletterSubscribeRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
    ) {}

    public record NewsletterSubscribeResponse(
        boolean ok,
        String message
    ) {}
}
