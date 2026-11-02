package com.growtrade.portfolio.service;

import com.growtrade.portfolio.dto.HoldingResponse;
import com.growtrade.portfolio.entity.HoldingEntity;
import com.growtrade.portfolio.repository.HoldingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.growtrade.portfolio.event.TradeExecutedEvent;
import com.growtrade.portfolio.entity.ProcessedTradeEventEntity;
import com.growtrade.portfolio.repository.ProcessedTradeEventRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class HoldingService {


    private final HoldingRepository holdingRepository;
    private final ProcessedTradeEventRepository processedTradeEventRepository;

    public HoldingService(
            HoldingRepository holdingRepository,
            ProcessedTradeEventRepository processedTradeEventRepository
    ) {
        this.holdingRepository = holdingRepository;
        this.processedTradeEventRepository = processedTradeEventRepository;
    }

    @Transactional
    public void processTrade(
            UUID userId,
            String symbol,
            String side,
            int quantity,
            BigDecimal executionPrice
    ) {
        if (userId == null || symbol == null || symbol.isBlank()
                || quantity <= 0 || executionPrice == null
                || executionPrice.signum() <= 0) {
            throw new IllegalArgumentException("Invalid trade details");
        }

        String normalizedSymbol = symbol.trim().toUpperCase();
        if (side == null || side.isBlank()) {
            throw new IllegalArgumentException("Trade side is required");
        }
        String normalizedSide = side.toUpperCase();

        if (!normalizedSide.equals("BUY")
                && !normalizedSide.equals("SELL")) {
            throw new IllegalArgumentException("Side must be BUY or SELL");
        }

        var existing = holdingRepository
                .findByUserIdAndSymbol(userId, normalizedSymbol);

        Instant now = Instant.now();

        if (normalizedSide.equals("BUY")) {

            if (existing.isEmpty()) {
                holdingRepository.save(new HoldingEntity(
                        UUID.randomUUID(),
                        userId,
                        normalizedSymbol,
                        quantity,
                        executionPrice,
                        now,
                        now
                ));
                return;
            }

            HoldingEntity holding = existing.get();

            int oldQuantity = holding.getQuantity();
            int newQuantity = Math.addExact(oldQuantity, quantity);

            BigDecimal oldValue = holding.getAveragePrice()
                    .multiply(BigDecimal.valueOf(oldQuantity));

            BigDecimal newValue = executionPrice
                    .multiply(BigDecimal.valueOf(quantity));

            BigDecimal averagePrice = oldValue.add(newValue)
                    .divide(BigDecimal.valueOf(newQuantity),
                            4, RoundingMode.HALF_UP);

            holding.updateHolding(newQuantity, averagePrice, now);
            return;
        }

        if (existing.isEmpty()
                || existing.get().getQuantity() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient holdings to sell " + normalizedSymbol
            );
        }

        HoldingEntity holding = existing.get();

        int remainingQuantity = holding.getQuantity() - quantity;

        if (remainingQuantity == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.updateHolding(
                    remainingQuantity,
                    holding.getAveragePrice(),
                    now
            );
        }
    }

    @Transactional
    public void processTradeEvent(TradeExecutedEvent event) {

        if (event == null || event.executionId() == null) {
            throw new IllegalArgumentException("Invalid trade execution event");
        }

        // Step 1: Check duplicate event
        if (processedTradeEventRepository.existsById(event.executionId())) {
            return;
        }

        // Step 2: Update portfolio holding
        processTrade(
                event.userId(),
                event.symbol(),
                event.side(),
                event.quantity(),
                event.executionPrice()
        );

        // Step 3: Mark event as processed
        processedTradeEventRepository.save(
                new ProcessedTradeEventEntity(
                        event.executionId(),
                        Instant.now()
                )
        );
    }


    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldings(UUID userId) {
        return holdingRepository.findAllByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private HoldingResponse toResponse(HoldingEntity holding) {
        BigDecimal investedAmount = holding.getAveragePrice()
                .multiply(BigDecimal.valueOf(holding.getQuantity()));

        return new HoldingResponse(
                holding.getUserId(),
                holding.getSymbol(),
                holding.getQuantity(),
                holding.getAveragePrice(),
                investedAmount
        );
    }
}