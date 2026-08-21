package com.civilsupplies.api.service;

import com.civilsupplies.api.common.dto.PageResponse;
import com.civilsupplies.api.common.exception.ResourceNotFoundException;
import com.civilsupplies.api.dto.QuoteDtos.QuoteCreateRequest;
import com.civilsupplies.api.dto.QuoteDtos.QuoteResponse;
import com.civilsupplies.api.entity.Quote;
import com.civilsupplies.api.entity.QuoteStatus;
import com.civilsupplies.api.repository.QuoteRepository;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final StorageService storageService;
    private final EmailService emailService;

    public QuoteService(QuoteRepository quoteRepository, StorageService storageService, EmailService emailService) {
        this.quoteRepository = quoteRepository;
        this.storageService = storageService;
        this.emailService = emailService;
    }

    @Transactional
    public QuoteResponse createQuote(QuoteCreateRequest request, MultipartFile boqFile) {
        Quote quote = new Quote();
        quote.setName(request.name());
        quote.setPhone(request.phone());
        quote.setEmail(request.email());
        quote.setProjectDetails(request.projectDetails());
        quote.setSiteLocation(request.siteLocation());
        quote.setTimeline(request.timeline());
        quote.setStatus(QuoteStatus.NEW);

        if (boqFile != null && !boqFile.isEmpty()) {
            String storedFilename = storageService.store(boqFile);
            quote.setBoqFilename(boqFile.getOriginalFilename());
            quote.setBoqFileUrl(storageService.getFileUrl(storedFilename));
        }

        Quote saved = quoteRepository.save(quote);
        emailService.sendQuoteNotification(saved);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuoteResponse> listQuotes(int page, int size, QuoteStatus status) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Quote> quotesPage = (status != null)
                ? quoteRepository.findByStatus(status, pageable)
                : quoteRepository.findAll(pageable);

        var content = quotesPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                quotesPage.getTotalElements(),
                quotesPage.getTotalPages(),
                quotesPage.getNumber(),
                quotesPage.getSize(),
                quotesPage.isFirst(),
                quotesPage.isLast()
        );
    }

    @Transactional
    public QuoteResponse updateQuoteStatus(Long id, QuoteStatus status) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", "id", id));
        quote.setStatus(status);
        Quote updated = quoteRepository.save(quote);
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public Quote getQuote(Long id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote", "id", id));
    }

    @Transactional(readOnly = true)
    public Resource getBoqResource(String filename) {
        return storageService.loadAsResource(filename);
    }

    private QuoteResponse toResponse(Quote quote) {
        return new QuoteResponse(
                quote.getId(),
                quote.getName(),
                quote.getPhone(),
                quote.getEmail(),
                quote.getProjectDetails(),
                quote.getSiteLocation(),
                quote.getTimeline(),
                quote.getBoqFilename(),
                quote.getBoqFileUrl(),
                quote.getStatus(),
                quote.getCreatedAt()
        );
    }
}
