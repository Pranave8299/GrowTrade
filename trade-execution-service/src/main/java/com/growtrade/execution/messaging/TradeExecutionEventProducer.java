
package com.growtrade.execution.messaging;

import com.growtrade.execution.event.TradeExecutedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeExecutionEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(TradeExecutionEventProducer.class);

    private static final String TOPIC = "trade-executed";

    private final KafkaTemplate<String, TradeExecutedEvent> kafkaTemplate;

    public TradeExecutionEventProducer(
            KafkaTemplate<String, TradeExecutedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTradeExecutedEvent(TradeExecutedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error(
                        "Failed to publish trade execution event. OrderId: {}",
                        event.orderId(),
                        exception
                );
                return;
            }

            log.info(
                    "Trade execution event published. Topic: {}, OrderId: {}",
                    TOPIC,
                    event.orderId()
            );
        });
    }
}
