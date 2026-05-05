package com.flashcart.order.api;

import com.flashcart.order.app.OrderService;
import com.flashcart.order.domain.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public OrderResponse create(@RequestBody CreateOrderRequest req,
                                @RequestHeader("X-User-Email") String email) {
        Order order = service.createOrder(req, email);
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getUserId(),
                order.getStatus()
        );
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(@RequestHeader("X-User-Email") String email) {
        return service.getOrdersByUser(email).stream()
                .map(o -> new OrderResponse(
                        o.getId(),
                        o.getProductId(),
                        o.getQuantity(),
                        o.getUserId(),
                        o.getStatus()
                ))
                .toList();
    }
}