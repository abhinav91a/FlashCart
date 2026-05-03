package com.flashcart.product.messaging;

import com.flashcart.common.events.StockUpdatedEvent;
import com.flashcart.product.domain.Product;
import com.flashcart.product.domain.ProductRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockUpdatedConsumer {

    private final ProductRepository repo;

    public StockUpdatedConsumer(ProductRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "stock-updated", groupId = "product-service")
    public void consume(StockUpdatedEvent event) {
        repo.findById(event.getProductId()).ifPresent(product -> {
            product.setStock(event.getNewStock());
            repo.save(product);
            System.out.println("Product stock updated: " + event.getProductId() + " → " + event.getNewStock());
        });
    }
}