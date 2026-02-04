package com.surgegate.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    @Autowired private StringRedisTemplate redisTemplate;

    // Called when Organizer creates an event
    public void initStock(String eventId, String ticketType, int count) {
        String key = "stock:" + eventId + ":" + ticketType;
        redisTemplate.opsForValue().set(key, String.valueOf(count));
    }

    // Called when Attendee buys a ticket
    public boolean deductStock(String eventId, String ticketType) {
        String key = "stock:" + eventId + ":" + ticketType;
        Long remaining = redisTemplate.opsForValue().decrement(key);
        if (remaining != null && remaining >= 0) return true;

        // Rollback if we went below 0
        redisTemplate.opsForValue().increment(key);
        return false;
    }
}
