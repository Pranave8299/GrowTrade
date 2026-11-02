package com.growtrade.orders.event;
import java.time.Instant;
import java.util.UUID;
public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        UUID userId,
        String symbol,
        String side,
        int quantity,
        String status,
        Instant occurredAt
) {
}