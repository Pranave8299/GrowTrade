package com.growtrade.orders.controller;

import com.growtrade.orders.dto.CreateOrderRequest;
import com.growtrade.orders.dto.OrderResponse;
import com.growtrade.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable UUID orderId) {

        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderResponse> getOrdersByUserId(
            @RequestParam UUID userId) {

        return orderService.getOrdersByUserId(userId);
    }
}