package com.growtrade.execution.consumer;




import com.growtrade.execution.event.OrderCreatedEvent;
import com.growtrade.execution.model.TradeExecutionResult;
import com.growtrade.execution.service.TradeExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedEventConsumer.class);

    private final TradeExecutionService tradeExecutionService;

    public OrderCreatedEventConsumer(
            TradeExecutionService tradeExecutionService) {
        this.tradeExecutionService = tradeExecutionService;
    }

    @KafkaListener(
            topics = "order-created",
            groupId = "trade-execution-group"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {

        log.info("Received Order Created Event: {}", event.orderId());

        TradeExecutionResult result =
                tradeExecutionService.executeTrade(event);

        log.info("========== TRADE EXECUTION RESULT ==========");
        log.info("Execution ID: {}", result.executionId());
        log.info("Order ID: {}", result.orderId());
        log.info("Symbol: {}", result.symbol());
        log.info("Side: {}", result.side());
        log.info("Quantity: {}", result.quantity());
        log.info("Execution Price: {}", result.executionPrice());
        log.info("Total Amount: {}", result.totalAmount());
        log.info("Execution Status: {}", result.status());
        log.info("Executed At: {}", result.executedAt());
        log.info("============================================");
    }
}