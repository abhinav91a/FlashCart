package com.flashcart.order.app;

import com.flashcart.common.events.OrderPlacedEvent;
import com.flashcart.order.api.CreateOrderRequest;
import com.flashcart.order.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repo;
    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, OrderPlacedEvent> kafka;

    public OrderService(OrderRepository repo,
                        StringRedisTemplate redis,
                        KafkaTemplate<String, OrderPlacedEvent> kafka) {
        this.repo = repo;
        this.redis = redis;
        this.kafka = kafka;
    }

    public Order createOrder(CreateOrderRequest req, String email) {
        String key = "product:" + req.productId() + ":stock";
        Long newStock = redis.opsForValue().decrement(key, req.quantity());

        if (newStock == null || newStock < 0) {
            redis.opsForValue().increment(key, req.quantity());
            throw new RuntimeException("Not enough stock");
        }

        Order order = Order.builder()
                .productId(req.productId())
                .quantity(req.quantity())
                .userId(email)
                .status(OrderStatus.PENDING)
                .build();

        Order saved = repo.save(order);

        kafka.send("order-placed", OrderPlacedEvent.builder()
                .orderId(saved.getId())
                .productId(saved.getProductId())
                .quantity(saved.getQuantity())
                .userId(email)
                .build());

        saved.setStatus(OrderStatus.CONFIRMED);
        return repo.save(saved);
    }

    public List<Order> getOrdersByUser(String userId) {
        return repo.findByUserIdOrderByIdDesc(userId);
    }
}