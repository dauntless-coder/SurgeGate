package com.surgegate.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.entities.Ticket;
import com.surgegate.backend.entities.TicketStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurgeGateService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 1. LOAD STOCK (Called by EventService)
    public void initStock(String eventId, String typeId, int count) {
        String key = "stock:" + eventId + ":" + typeId;
        redisTemplate.opsForValue().set(key, String.valueOf(count));
        log.info("Initialized stock for key: {} with count: {}", key, count);
    }

    // 2. ATOMIC PURCHASE (Called when User clicks Buy)
    public String attemptPurchase(String eventId, String typeId, String userId) {
        String key = "stock:" + eventId + ":" + typeId;

        // --- THE GATEKEEPER ---
        Long remaining = redisTemplate.opsForValue().decrement(key);

        if (remaining != null && remaining >= 0) {
            String ticketId = UUID.randomUUID().toString();
            try {
                // Construct the actual Ticket entity
                Ticket ticket = Ticket.builder()
                        .id(ticketId)
                        .eventId(eventId)
                        .ticketTypeId(typeId)
                        .userId(userId)
                        .status(TicketStatusEnum.PURCHASED) // Assume success once past Redis
                        .build();

                String json = objectMapper.writeValueAsString(ticket);

                // Send to Kafka for Async Persistence
                kafkaTemplate.send("orders_topic", json);

                log.info("Stock reserved. Ticket {} queued for processing.", ticketId);
                return ticketId;
            } catch (Exception e) {
                log.error("Error queueing ticket", e);
                redisTemplate.opsForValue().increment(key); // Rollback
                return null;
            }
        } else {
            redisTemplate.opsForValue().increment(key); // Rollback negative numbers
            return null; // SOLD OUT
        }
    }
}