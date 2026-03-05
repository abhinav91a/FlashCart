package com.flashcart.common.events;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPlacedEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String userId;
}
