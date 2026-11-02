package com.growtrade.orders.messaging;

import com.growtrade.orders.config.KafkaTopicConfig;
import com.growtrade.orders.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopicConfig.ORDER_CREATED_TOPIC,
                event.orderId().toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error(
                        "Failed to publish order event: {}",
                        event.orderId(),
                        exception
                );
                return;
            }

            log.info(
                    "Order event published successfully. OrderId: {}, Partition: {}",
                    event.orderId(),
                    result.getRecordMetadata().partition()
            );
        });
    }
}