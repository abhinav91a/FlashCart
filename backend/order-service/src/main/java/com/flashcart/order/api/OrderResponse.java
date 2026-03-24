package com.flashcart.order.api;

import com.flashcart.order.domain.OrderStatus;

public record OrderResponse(
        Long id,
        Long productId,
        Integer quantity,
        String userId,
        OrderStatus status
) {}
