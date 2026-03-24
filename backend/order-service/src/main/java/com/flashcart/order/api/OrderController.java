package com.flashcart.order.api;

import com.flashcart.order.app.OrderService;
import com.flashcart.order.domain.Order;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public OrderResponse create(@RequestBody CreateOrderRequest req) {
        Order order = service.createOrder(req);
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getUserId(),
                order.getStatus()
        );
    }
}
