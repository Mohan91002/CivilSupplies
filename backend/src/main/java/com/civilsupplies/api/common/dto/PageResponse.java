package com.civilsupplies.api.common.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int number,
    int size,
    boolean first,
    boolean last
) {
    public static <T> PageResponse<T> of(List<T> content, long totalElements, int totalPages, int number, int size, boolean first, boolean last) {
        return new PageResponse<>(content, totalElements, totalPages, number, size, first, last);
    }
}
