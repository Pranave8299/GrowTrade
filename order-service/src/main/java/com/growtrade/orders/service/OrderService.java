package com.growtrade.orders.service;
import com.growtrade.orders.dto.CreateOrderRequest;
import com.growtrade.orders.dto.OrderResponse;
import com.growtrade.orders.entity.OrderEntity;
import com.growtrade.orders.exception.OrderNotFoundException;
import com.growtrade.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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