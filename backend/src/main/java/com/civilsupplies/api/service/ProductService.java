package com.civilsupplies.api.service;

import com.civilsupplies.api.common.dto.PageResponse;
import com.civilsupplies.api.common.exception.ResourceNotFoundException;
import com.civilsupplies.api.dto.ProductDtos.ProductResponse;
import com.civilsupplies.api.entity.Product;
import com.civilsupplies.api.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> listProducts(String categorySlug, String searchQuery, int page, int size, String sort) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), sortObj);

        Page<Product> productsPage = productRepository.findActiveProducts(categorySlug, searchQuery, pageable);

        var content = productsPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.isFirst(),
                productsPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        return toResponse(product);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getBrand(),
                product.getUnit(),
                product.getDescription(),
                product.getImageUrl(),
                product.isActive(),
                product.getCreatedAt()
        );
    }
}
