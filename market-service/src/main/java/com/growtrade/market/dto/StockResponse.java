package com.growtrade.market.dto;

import java.math.BigDecimal;

public record StockResponse(
        String symbol,
        String companyName,
        String exchange,
        String sector,
        BigDecimal currentPrice
) {}