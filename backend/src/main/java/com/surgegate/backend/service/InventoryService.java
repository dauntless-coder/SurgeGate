package com.surgegate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class InventoryService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Initialize stock to 5 tickets when the app starts
    @PostConstruct
    public void initStock() {
        redisTemplate.opsForValue().set("event_stock:concert_123", "5");
        System.out.println("--- STOCK RESET TO 5 ---");
    }

    public boolean deductStock(String eventId) {
        // Atomic Decrement: Safe even with 1000 concurrent requests
        Long stock = redisTemplate.opsForValue().decrement("event_stock:" + eventId);
        return stock != null && stock >= 0;
    }
}
