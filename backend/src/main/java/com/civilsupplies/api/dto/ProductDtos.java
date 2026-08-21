package com.civilsupplies.api.dto;

import java.time.LocalDateTime;

public final class ProductDtos {

    private ProductDtos() {}

    public record ProductResponse(
        Long id,
        String name,
        String slug,
        Long categoryId,
        String categoryName,
        String brand,
        String unit,
        String description,
        String imageUrl,
        boolean isActive,
        LocalDateTime createdAt
    ) {}
}
