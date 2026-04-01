package com.flashcart.notification.api;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    private final SimpMessagingTemplate messaging;

    public NotificationController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @PostMapping
    public void send(@RequestBody String msg) {
        messaging.convertAndSend("/topic/notifications", msg);
    }
}
