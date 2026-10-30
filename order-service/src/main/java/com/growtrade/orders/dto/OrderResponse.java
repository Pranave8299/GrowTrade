package com.growtrade.orders.dto;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID userId,
        String symbol,
        String side,
        int quantity,
        String status,
        OffsetDateTime createdAt
) {
}
