package com.flashcart.common.events;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdatedEvent {
    private Long productId;
    private Integer newStock;
}