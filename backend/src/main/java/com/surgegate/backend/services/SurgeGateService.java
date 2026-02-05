package com.surgegate.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.domain.entities.Ticket;
import com.surgegate.backend.domain.entities.enums.TicketStatus;
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

    public void initStock(String eventId, String typeId, int count) {
        String key = "stock:" + eventId + ":" + typeId;
        redisTemplate.opsForValue().set(key, String.valueOf(count));
        log.info("Initialized stock for key: {} with count: {}", key, count);
    }

    public String attemptPurchase(String eventId, String typeId, String userId) {
        String key = "stock:" + eventId + ":" + typeId;

        Long remaining = redisTemplate.opsForValue().decrement(key);

        if (remaining != null && remaining >= 0) {
            String ticketId = UUID.randomUUID().toString();
            try {
                Ticket ticket = Ticket.builder()
                        .id(ticketId)
                        .eventId(eventId)
                        .ticketTypeId(typeId)
                        .userId(userId)
                        .status(TicketStatus.CONFIRMED)
                        .build();

                String json = objectMapper.writeValueAsString(ticket);

                kafkaTemplate.send("orders_topic", json);

                log.info("Stock reserved. Ticket {} queued for processing.", ticketId);
                return ticketId;
            } catch (Exception e) {
                log.error("Error queueing ticket", e);
                redisTemplate.opsForValue().increment(key);
                return null;
            }
        } else {
            redisTemplate.opsForValue().increment(key);
            return null;
        }
    }
}
