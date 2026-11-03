package com.growtrade.wallet.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletTransactionResponse(
        UUID transactionId,
        String transactionType,
        BigDecimal amount,
        UUID referenceId,
        String description,
        Instant createdAt
) {
}