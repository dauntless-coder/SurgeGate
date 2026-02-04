package com.surgegate.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurgeGateService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 1. Init Stock (Called by Admin)
    public void initStock(String eventId, String typeId, int count) {
        redisTemplate.opsForValue().set("stock:" + eventId + ":" + typeId, String.valueOf(count));
    }

    // 2. Atomic Purchase (Called by User)
    public String attemptPurchase(String eventId, String typeId, String userId) {
        String key = "stock:" + eventId + ":" + typeId;

        // --- THE SURGEGATE CHECK ---
        Long remaining = redisTemplate.opsForValue().decrement(key);

        if (remaining != null && remaining >= 0) {
            String ticketId = UUID.randomUUID().toString();

            // Send to Kafka
            try {
                Ticket ticket = new Ticket(ticketId, eventId, typeId, userId, "PENDING");
                String json = objectMapper.writeValueAsString(ticket);
                kafkaTemplate.send("orders_topic", json);
            } catch (Exception e) {
                redisTemplate.opsForValue().increment(key); // Rollback
                return null;
            }
            return ticketId;
        } else {
            redisTemplate.opsForValue().increment(key); // Rollback negative
            return null; // SOLD OUT
        }
    }
}