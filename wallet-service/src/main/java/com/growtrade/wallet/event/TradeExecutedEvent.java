
package com.growtrade.wallet.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeExecutedEvent(
        UUID executionId,
        UUID orderId,
        UUID userId,
        String symbol,
        String side,
        int quantity,
        BigDecimal executionPrice,
        BigDecimal totalAmount,
        String status,
        Instant executedAt
) {
}
