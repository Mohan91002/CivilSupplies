package com.civilsupplies.api.repository;

import com.civilsupplies.api.entity.Quote;
import com.civilsupplies.api.entity.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    Page<Quote> findByStatus(QuoteStatus status, Pageable pageable);
}
