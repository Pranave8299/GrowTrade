package com.growtrade.orders.service;
import com.growtrade.orders.dto.CreateOrderRequest;
import com.growtrade.orders.dto.OrderResponse;
import com.growtrade.orders.entity.OrderEntity;
import com.growtrade.orders.event.OrderCreatedEvent;
import com.growtrade.orders.exception.OrderNotFoundException;
import com.growtrade.orders.messaging.OrderEventProducer;
import com.growtrade.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.time.Instant;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderService(
            OrderRepository orderRepository,
            OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        OrderEntity order = new OrderEntity(
                UUID.randomUUID(),
                request.userId(),
                request.symbol().toUpperCase(Locale.ROOT),
                request.side(),
                request.quantity(),
                "PENDING"
        );

        OrderEntity savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getSymbol(),
                savedOrder.getSide(),
                savedOrder.getQuantity(),
                savedOrder.getStatus(),
                Instant.now()
        );

        orderEventProducer.publishOrderCreatedEvent(event);

        return toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new OrderNotFoundException(orderId)
                );

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        return orderRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getSymbol(),
                order.getSide(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}