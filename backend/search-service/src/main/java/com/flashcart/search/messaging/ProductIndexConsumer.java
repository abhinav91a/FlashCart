package com.flashcart.search.messaging;

import com.flashcart.common.events.ProductIndexEvent;
import com.flashcart.search.app.SearchService;
import com.flashcart.search.domain.ProductDocument;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductIndexConsumer {

    private final SearchService searchService;

    public ProductIndexConsumer(SearchService searchService) {
        this.searchService = searchService;
    }

    @KafkaListener(topics = "product-index", groupId = "search-service")
    public void consume(ProductIndexEvent event) {
        ProductDocument doc = ProductDocument.builder()
                .id(String.valueOf(event.getId()))
                .name(event.getName())
                .description(event.getDescription())
                .sku(event.getSku())
                .priceInCents(event.getPriceInCents())
                .stock(event.getStock())
                .flashDeal(event.getFlashDeal())
                .build();

        searchService.index(doc);
        System.out.println("Indexed product: " + event.getName());
    }
}