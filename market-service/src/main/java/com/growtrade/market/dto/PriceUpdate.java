package com.growtrade.market.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceUpdate(
        String symbol,
        BigDecimal price,
        BigDecimal change,
        BigDecimal changePercent,
        Instant updatedAt,
        boolean simulated
) {}