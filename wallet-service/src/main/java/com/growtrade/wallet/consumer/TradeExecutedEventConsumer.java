
package com.growtrade.wallet.consumer;

import com.growtrade.wallet.event.TradeExecutedEvent;
import com.growtrade.wallet.service.WalletService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TradeExecutedEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(TradeExecutedEventConsumer.class);

    private final WalletService walletService;

    public TradeExecutedEventConsumer(WalletService walletService) {
        this.walletService = walletService;
    }

    @KafkaListener(
            topics = "trade-executed",
            groupId = "wallet-group"
    )
    public void consume(TradeExecutedEvent event) {

        log.info(
                "Received trade event. Execution ID: {}, Symbol: {}, Side: {}",
                event.executionId(),
                event.symbol(),
                event.side()
        );

        walletService.processTradeExecutedEvent(event);

        log.info(
                "Trade event processing completed. Execution ID: {}",
                event.executionId()
        );
    }
}
