package com.flashcart.product.api;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Long priceInCents,
        Integer stock,
        Boolean flashDeal
) {}