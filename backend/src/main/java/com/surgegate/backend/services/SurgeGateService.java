package com.surgegate.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.entities.Ticket;
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

    // 1. LOAD STOCK (Called when Admin creates event)
    public void initStock(String eventId, String typeId, int count) {
        String key = "stock:" + eventId + ":" + typeId;
        redisTemplate.opsForValue().set(key, String.valueOf(count));
    }

    // 2. ATOMIC PURCHASE (Called when User clicks Buy)
    public String attemptPurchase(String eventId, String typeId, String userId) {
        String key = "stock:" + eventId + ":" + typeId;

        // --- THE GATEKEEPER ---
        Long remaining = redisTemplate.opsForValue().decrement(key);

        if (remaining != null && remaining >= 0) {
            String ticketId = UUID.randomUUID().toString();
            try {
                // Send to Kafka for async processing
                Ticket ticket = Ticket.builder()
                        .id(ticketId)
                        .eventId(eventId)
                        .ticketTypeId(typeId)
                        .userId(userId)
                        .status("PENDING") // User sees "Processing"
                        .build();

                String json = objectMapper.writeValueAsString(ticket);
                kafkaTemplate.send("orders_topic", json);

                return ticketId;
            } catch (Exception e) {
                redisTemplate.opsForValue().increment(key); // Rollback
                return null;
            }
        } else {
            redisTemplate.opsForValue().increment(key); // Rollback negative numbers
            return null; // SOLD OUT
        }
    }
}