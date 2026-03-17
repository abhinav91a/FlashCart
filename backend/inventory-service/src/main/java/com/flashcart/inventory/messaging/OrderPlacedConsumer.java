package com.flashcart.inventory.messaging;

import com.flashcart.common.events.OrderPlacedEvent;
import com.flashcart.inventory.domain.InventoryItem;
import com.flashcart.inventory.domain.InventoryRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderPlacedConsumer {

    private final InventoryRepository repo;
    private final StringRedisTemplate redis;

    public OrderPlacedConsumer(InventoryRepository repo, StringRedisTemplate redis) {
        this.repo = repo;
        this.redis = redis;
    }

    @KafkaListener(topics = "order-placed", groupId = "inventory-service")
    public void consume(OrderPlacedEvent event) {

        Long productId = event.getProductId();
        Integer qty = event.getQuantity();

        // Update Postgres
        InventoryItem item = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product " + productId));

        item.setStock(item.getStock() - qty);
        repo.save(item);

        // Update Redis
        String key = "product:" + productId + ":stock";
        redis.opsForValue().set(key, String.valueOf(item.getStock()));

        System.out.println("Inventory updated for product " + productId);
    }
}
