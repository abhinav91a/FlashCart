package com.flashcart.order.api;

public record CreateOrderRequest(
        Long productId,
        Integer quantity,
        String userId
) {}
