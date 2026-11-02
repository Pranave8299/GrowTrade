package com.growtrade.portfolio.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "holdings",
        schema = "portfolio",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holdings_user_symbol",
                        columnNames = {"user_id", "symbol"}
                )
        }
)
public class HoldingEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "average_price", nullable = false,
            precision = 19, scale = 4)
    private BigDecimal averagePrice;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected HoldingEntity() {
    }

    public HoldingEntity(
            UUID id,
            UUID userId,
            String symbol,
            int quantity,
            BigDecimal averagePrice,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public BigDecimal getAveragePrice() { return averagePrice; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateHolding(
            int quantity,
            BigDecimal averagePrice,
            Instant updatedAt
    ) {
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.updatedAt = updatedAt;
    }
}