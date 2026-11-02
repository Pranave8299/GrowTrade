package com.growtrade.market.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionCheck implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    public RedisConnectionCheck(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {

        redisTemplate.opsForValue()
                .set("growtrade:market:connection", "CONNECTED");

        String result = redisTemplate.opsForValue()
                .get("growtrade:market:connection");

        System.out.println("Redis Connection Status: " + result);
    }
}