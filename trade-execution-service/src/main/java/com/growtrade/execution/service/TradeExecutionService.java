
package com.growtrade.execution.service;

import com.growtrade.execution.entity.TradeExecutionEntity;
import com.growtrade.execution.event.OrderCreatedEvent;
import com.growtrade.execution.event.TradeExecutedEvent;
import com.growtrade.execution.exception.ExecutionNotFoundException;
import com.growtrade.execution.messaging.TradeExecutionEventProducer;
import com.growtrade.execution.model.TradeExecutionResult;
import com.growtrade.execution.repository.TradeExecutionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TradeExecutionService {

    // Temporary demo price. Not a real market price.
    private static final BigDecimal DEMO_EXECUTION_PRICE =
            new BigDecimal("2500.00");

    private final TradeExecutionRepository tradeExecutionRepository;
    private final TradeExecutionEventProducer eventProducer;

    public TradeExecutionService(
            TradeExecutionRepository tradeExecutionRepository,
            TradeExecutionEventProducer eventProducer) {

        this.tradeExecutionRepository = tradeExecutionRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public TradeExecutionResult executeTrade(OrderCreatedEvent event) {

        validateOrder(event);

        // Idempotency check: avoid executing the same order twice.
        var existingExecution =
                tradeExecutionRepository.findByOrderId(event.orderId());

        if (existingExecution.isPresent()) {
            return toTradeExecutionResult(existingExecution.get());
        }

        BigDecimal executionPrice = DEMO_EXECUTION_PRICE;

        BigDecimal totalAmount = executionPrice.multiply(
                BigDecimal.valueOf(event.quantity())
        );

        TradeExecutionResult result = new TradeExecutionResult(
                UUID.randomUUID(),
                event.orderId(),
                event.symbol(),
                event.side().toUpperCase(),
                event.quantity(),
                executionPrice,
                totalAmount,
                "FILLED",
                Instant.now()
        );

        TradeExecutionEntity entity = new TradeExecutionEntity(
                result.executionId(),
                result.orderId(),
                result.symbol(),
                result.side(),
                result.quantity(),
                result.executionPrice(),
                result.totalAmount(),
                result.status(),
                result.executedAt()
        );

        tradeExecutionRepository.save(entity);

        // Prepare event using the successfully created execution.
        TradeExecutedEvent tradeEvent = new TradeExecutedEvent(
                result.executionId(),
                result.orderId(),
                event.userId(),
                result.symbol(),
                result.side(),
                result.quantity(),
                result.executionPrice(),
                result.totalAmount(),
                result.status(),
                result.executedAt()
        );

        // Publish only after the database transaction commits.
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        eventProducer.publishTradeExecutedEvent(tradeEvent);
                    }
                }
        );

        return result;
    }

    private TradeExecutionResult toTradeExecutionResult(
            TradeExecutionEntity entity) {

        return new TradeExecutionResult(
                entity.getId(),
                entity.getOrderId(),
                entity.getSymbol(),
                entity.getSide(),
                entity.getQuantity(),
                entity.getExecutionPrice(),
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getExecutedAt()
        );
    }

    private void validateOrder(OrderCreatedEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("Order event cannot be null");
        }

        if (event.orderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (event.userId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (event.symbol() == null || event.symbol().isBlank()) {
            throw new IllegalArgumentException("Stock symbol is required");
        }

        if (event.side() == null ||
                !(event.side().equalsIgnoreCase("BUY")
                        || event.side().equalsIgnoreCase("SELL"))) {
            throw new IllegalArgumentException(
                    "Order side must be BUY or SELL"
            );
        }

        if (event.quantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    @Transactional(readOnly = true)
    public TradeExecutionResult getExecutionByOrderId(UUID orderId) {

        TradeExecutionEntity entity = tradeExecutionRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new ExecutionNotFoundException(
                        "Execution not found for order ID: " + orderId
                ));

        return toTradeExecutionResult(entity);
    }
}
