package com.growtrade.execution.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeExecutionResult(
        UUID executionId,
        UUID orderId,
        String symbol,
        String side,
        int quantity,
        BigDecimal executionPrice,
        BigDecimal totalAmount,
        String status,
        Instant executedAt
) {
}