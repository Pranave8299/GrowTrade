package com.growtrade.portfolio.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HoldingResponse(
        UUID userId,
        String symbol,
        int quantity,
        BigDecimal averagePrice,
        BigDecimal investedAmount
) {
}