package com.growtrade.portfolio.consumer;

import com.growtrade.portfolio.event.TradeExecutedEvent;
import com.growtrade.portfolio.service.HoldingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeExecutedEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(TradeExecutedEventConsumer.class);

    private final HoldingService holdingService;

    public TradeExecutedEventConsumer(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @KafkaListener(
            topics = "trade-executed",
            groupId = "portfolio-group"
    )
    public void consumeTradeExecutedEvent(TradeExecutedEvent event) {

        log.info(
                "Received Trade Executed Event. ExecutionId: {}, OrderId: {}",
                event.executionId(),
                event.orderId()
        );

        if (!"FILLED".equalsIgnoreCase(event.status())) {
            log.info(
                    "Ignoring trade event because status is: {}",
                    event.status()
            );
            return;
        }

        holdingService.processTradeEvent(event);

        log.info(
                "Portfolio updated successfully. UserId: {}, Symbol: {}, Side: {}, Quantity: {}",
                event.userId(),
                event.symbol(),
                event.side(),
                event.quantity()
        );
    }
}