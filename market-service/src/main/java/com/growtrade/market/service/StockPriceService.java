package com.growtrade.market.service;

import com.growtrade.market.dto.PriceUpdate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class StockPriceService {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    private final Map<String, BigDecimal> previousPrices =
            new ConcurrentHashMap<>();

    public StockPriceService(
            StringRedisTemplate redisTemplate,
            SimpMessagingTemplate messagingTemplate) {

        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;

        prices.put("RELIANCE", new BigDecimal("2500.00"));
        prices.put("TCS", new BigDecimal("3800.00"));
        prices.put("INFY", new BigDecimal("1500.00"));
        prices.put("HDFCBANK", new BigDecimal("1700.00"));
        prices.put("ICICIBANK", new BigDecimal("1200.00"));
        prices.put("TATAMOTORS", new BigDecimal("700.00"));

        previousPrices.putAll(prices);

        // Save initial prices in Redis.
        prices.forEach(this::savePriceInRedis);
    }

    @Scheduled(fixedRate = 1000)
    public void updateStockPrices() {

        prices.forEach((symbol, oldPrice) -> {

            BigDecimal movement = BigDecimal.valueOf(
                    ThreadLocalRandom.current()
                            .nextDouble(-1.00, 1.01)
            ).setScale(2, RoundingMode.HALF_UP);

            BigDecimal newPrice = oldPrice
                    .add(movement)
                    .max(new BigDecimal("0.01"))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal change = newPrice.subtract(
                    previousPrices.get(symbol)
            );

            BigDecimal changePercent = change
                    .multiply(BigDecimal.valueOf(100))
                    .divide(
                            previousPrices.get(symbol),
                            2,
                            RoundingMode.HALF_UP
                    );

            prices.put(symbol, newPrice);
            previousPrices.put(symbol, newPrice);

            PriceUpdate update = new PriceUpdate(
                    symbol,
                    newPrice,
                    change,
                    changePercent,
                    Instant.now(),
                    true
            );

            savePriceInRedis(symbol, newPrice);

            messagingTemplate.convertAndSend(
                    "/topic/market/prices",
                    update
            );
        });
    }

    private void savePriceInRedis(
            String symbol,
            BigDecimal price) {

        redisTemplate.opsForValue().set(
                "growtrade:market:price:" + symbol,
                price.toPlainString()
        );
    }

    public PriceUpdate getStockPrice(String symbol) {

        BigDecimal price = prices.get(symbol.toUpperCase());

        if (price == null) {
            throw new IllegalArgumentException(
                    "Stock not found: " + symbol
            );
        }

        return new PriceUpdate(
                symbol.toUpperCase(),
                price,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.now(),
                true
        );
    }
}