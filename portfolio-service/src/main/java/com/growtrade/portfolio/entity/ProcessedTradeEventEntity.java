package com.growtrade.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_trade_events", schema = "portfolio")
public class ProcessedTradeEventEntity {

    @Id
    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedTradeEventEntity() {
    }

    public ProcessedTradeEventEntity(UUID executionId, Instant processedAt) {
        this.executionId = executionId;
        this.processedAt = processedAt;
    }

    public UUID getExecutionId() {
        return executionId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}