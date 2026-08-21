package com.civilsupplies.api.dto;

public final class CategoryDtos {

    private CategoryDtos() {}

    public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String imageUrl,
        int sortOrder,
        Long productCount
    ) {}
}
