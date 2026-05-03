package com.flashcart.notification.messaging;

import com.flashcart.common.events.StockUpdatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderPlacedConsumer {

    private final SimpMessagingTemplate messaging;

    public OrderPlacedConsumer(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @KafkaListener(topics = "stock-updated", groupId = "notification-service")
    public void consume(StockUpdatedEvent event) {
        String message = "⚡ Product " + event.getProductId() +
                " stock updated → " + event.getNewStock() + " remaining";

        messaging.convertAndSend("/topic/notifications", message);
        System.out.println("Notification sent: " + message);
    }
}