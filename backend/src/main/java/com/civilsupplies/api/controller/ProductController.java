package com.civilsupplies.api.controller;

import com.civilsupplies.api.common.constant.AppConstants;
import com.civilsupplies.api.common.dto.PageResponse;
import com.civilsupplies.api.dto.ProductDtos.ProductResponse;
import com.civilsupplies.api.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        PageResponse<ProductResponse> products = productService.listProducts(category, q, page, size, sort);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String slug) {
        ProductResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(product);
    }
}
