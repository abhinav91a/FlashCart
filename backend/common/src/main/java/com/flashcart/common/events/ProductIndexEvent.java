package com.flashcart.common.events;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductIndexEvent {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private Long priceInCents;
    private Integer stock;
    private Boolean flashDeal;
}