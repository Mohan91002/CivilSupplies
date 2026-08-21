package com.civilsupplies.api.repository;

import com.civilsupplies.api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySlugAndIsActiveTrue(String slug);

    Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
           "AND (:categorySlug IS NULL OR p.category.slug = :categorySlug) " +
           "AND (:searchQuery IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
           "     OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchQuery, '%')) " +
           "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    Page<Product> findActiveProducts(
            @Param("categorySlug") String categorySlug,
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );

    long countByCategoryId(Long categoryId);
}
