package com.flashcart.notification.messaging;

import com.flashcart.common.events.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderPlacedConsumer {

    private final SimpMessagingTemplate messaging;

    public OrderPlacedConsumer(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @KafkaListener(topics = "order-placed", groupId = "notification-service")
    public void consume(OrderPlacedEvent event) {

        String message = "Order " + event.getOrderId() +
                " placed for product " + event.getProductId() +
                " (qty: " + event.getQuantity() + ")";

        messaging.convertAndSend("/topic/notifications", message);

        System.out.println("Notification sent: " + message);
    }
}
