package com.civilsupplies.api.service;

import com.civilsupplies.api.common.dto.PageResponse;
import com.civilsupplies.api.common.exception.ResourceNotFoundException;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryCreateRequest;
import com.civilsupplies.api.dto.EnquiryDtos.EnquiryResponse;
import com.civilsupplies.api.entity.Enquiry;
import com.civilsupplies.api.entity.EnquiryStatus;
import com.civilsupplies.api.repository.EnquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final EmailService emailService;

    public EnquiryService(EnquiryRepository enquiryRepository, EmailService emailService) {
        this.enquiryRepository = enquiryRepository;
        this.emailService = emailService;
    }

    @Transactional
    public EnquiryResponse createEnquiry(EnquiryCreateRequest request) {
        Enquiry enquiry = new Enquiry();
        enquiry.setName(request.name());
        enquiry.setPhone(request.phone());
        enquiry.setEmail(request.email());
        enquiry.setCity(request.city());
        enquiry.setProjectType(request.projectType());
        if (request.materials() != null && !request.materials().isEmpty()) {
            enquiry.setMaterials(String.join(",", request.materials()));
        }
        enquiry.setQuantity(request.quantity());
        enquiry.setMessage(request.message());
        enquiry.setStatus(EnquiryStatus.NEW);

        Enquiry saved = enquiryRepository.save(enquiry);
        emailService.sendEnquiryNotification(saved);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<EnquiryResponse> listEnquiries(int page, int size, EnquiryStatus status) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Enquiry> enquiriesPage = (status != null)
                ? enquiryRepository.findByStatus(status, pageable)
                : enquiryRepository.findAll(pageable);

        var content = enquiriesPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                enquiriesPage.getTotalElements(),
                enquiriesPage.getTotalPages(),
                enquiriesPage.getNumber(),
                enquiriesPage.getSize(),
                enquiriesPage.isFirst(),
                enquiriesPage.isLast()
        );
    }

    @Transactional
    public EnquiryResponse updateEnquiryStatus(Long id, EnquiryStatus status) {
        Enquiry enquiry = enquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquiry", "id", id));
        enquiry.setStatus(status);
        Enquiry updated = enquiryRepository.save(enquiry);
        return toResponse(updated);
    }

    private EnquiryResponse toResponse(Enquiry enquiry) {
        List<String> materialsList = null;
        if (enquiry.getMaterials() != null && !enquiry.getMaterials().isBlank()) {
            materialsList = Arrays.stream(enquiry.getMaterials().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return new EnquiryResponse(
                enquiry.getId(),
                enquiry.getName(),
                enquiry.getPhone(),
                enquiry.getEmail(),
                enquiry.getCity(),
                enquiry.getProjectType(),
                materialsList,
                enquiry.getQuantity(),
                enquiry.getMessage(),
                enquiry.getStatus(),
                enquiry.getCreatedAt()
        );
    }
}
