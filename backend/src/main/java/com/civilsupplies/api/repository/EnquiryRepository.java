package com.civilsupplies.api.repository;

import com.civilsupplies.api.entity.Enquiry;
import com.civilsupplies.api.entity.EnquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    Page<Enquiry> findByStatus(EnquiryStatus status, Pageable pageable);
}
