package com.civilsupplies.api.controller;

import com.civilsupplies.api.dto.NewsletterDtos.NewsletterSubscribeRequest;
import com.civilsupplies.api.dto.NewsletterDtos.NewsletterSubscribeResponse;
import com.civilsupplies.api.service.NewsletterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<NewsletterSubscribeResponse> subscribe(@Valid @RequestBody NewsletterSubscribeRequest request) {
        NewsletterSubscribeResponse response = newsletterService.subscribe(request);
        return ResponseEntity.ok(response);
    }
}
