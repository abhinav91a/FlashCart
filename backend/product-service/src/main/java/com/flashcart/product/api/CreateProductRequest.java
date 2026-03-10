package com.flashcart.product.api;

public record CreateProductRequest(
        String sku,
        String name,
        String description,
        Long priceInCents,
        Integer stock
) {}
