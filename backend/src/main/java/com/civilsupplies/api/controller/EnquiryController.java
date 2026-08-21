package com.civilsupplies.api.controller;

import com.civilsupplies.api.common.constant.AppConstants;
import com.civilsupplies.api.common.dto.PageResponse;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryCreateRequest;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryResponse;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryStatusUpdateRequest;
import com.civilsupplies.api.entity.EnquiryStatus;
import com.civilsupplies.api.service.EnquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @PostMapping
    public ResponseEntity<EnquiryResponse> submitEnquiry(@Valid @RequestBody EnquiryCreateRequest request) {
        EnquiryResponse response = enquiryService.createEnquiry(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<EnquiryResponse>> listEnquiries(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) EnquiryStatus status) {
        PageResponse<EnquiryResponse> enquiries = enquiryService.listEnquiries(page, size, status);
        return ResponseEntity.ok(enquiries);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnquiryResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody EnquiryStatusUpdateRequest request) {
        EnquiryResponse response = enquiryService.updateEnquiryStatus(id, request.status());
        return ResponseEntity.ok(response);
    }
}
