package com.growtrade.execution.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "trade_executions",
        schema = "trade_execution"
)
public class TradeExecutionEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, length = 10)
    private String side;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "execution_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal executionPrice;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    protected TradeExecutionEntity() {
    }

    public TradeExecutionEntity(
            UUID id,
            UUID orderId,
            String symbol,
            String side,
            int quantity,
            BigDecimal executionPrice,
            BigDecimal totalAmount,
            String status,
            Instant executedAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.executionPrice = executionPrice;
        this.totalAmount = totalAmount;
        this.status = status;
        this.executedAt = executedAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getSymbol() { return symbol; }
    public String getSide() { return side; }
    public int getQuantity() { return quantity; }
    public BigDecimal getExecutionPrice() { return executionPrice; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Instant getExecutedAt() { return executedAt; }
}