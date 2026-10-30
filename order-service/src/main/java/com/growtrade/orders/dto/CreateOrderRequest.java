package com.growtrade.orders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID userId,

        @NotBlank
        @Pattern(regexp = "[A-Za-z0-9.]{1,20}")
        String symbol,

        @NotBlank
        @Pattern(regexp = "BUY|SELL")
        String side,

        @Min(1)
        int quantity
) {}