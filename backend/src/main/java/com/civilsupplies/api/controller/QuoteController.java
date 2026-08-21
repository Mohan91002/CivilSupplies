package com.civilsupplies.api.controller;

import com.civilsupplies.api.common.constant.AppConstants;
import com.civilsupplies.api.common.dto.PageResponse;
import com.civilsupplies.api.dto.QuoteDtos.QuoteCreateRequest;
import com.civilsupplies.api.dto.QuoteDtos.QuoteResponse;
import com.civilsupplies.api.dto.QuoteDtos.QuoteStatusUpdateRequest;
import com.civilsupplies.api.entity.Quote;
import com.civilsupplies.api.entity.QuoteStatus;
import com.civilsupplies.api.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuoteResponse> submitQuote(
            @Valid @RequestPart("quote") QuoteCreateRequest request,
            @RequestPart(value = "boq", required = false) MultipartFile boqFile) {
        QuoteResponse response = quoteService.createQuote(request, boqFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<QuoteResponse>> listQuotes(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) QuoteStatus status) {
        PageResponse<QuoteResponse> quotes = quoteService.listQuotes(page, size, status);
        return ResponseEntity.ok(quotes);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<QuoteResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody QuoteStatusUpdateRequest request) {
        QuoteResponse response = quoteService.updateQuoteStatus(id, request.status());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/boq")
    public ResponseEntity<Map<String, String>> getBoqUrl(@PathVariable Long id) {
        Quote quote = quoteService.getQuote(id);
        String url = quote.getBoqFileUrl() != null ? quote.getBoqFileUrl() : "";
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> downloadBoqFile(@PathVariable String filename) {
        Resource resource = quoteService.getBoqResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
