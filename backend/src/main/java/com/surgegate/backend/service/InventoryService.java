package com.surgegate.backend.service;

import com.surgegate.backend.model.Concert;
import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.repository.ConcertRepository;
import com.surgegate.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private ConcertRepository concertRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

    // Redis key patterns
    private static final String STOCK_KEY_PREFIX = "event_stock:";
    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final long LOCK_TIMEOUT_SECONDS = 5;

    // Atomic decrement with distributed lock to handle thundering herd
    public boolean deductStock(String concertId) {
        String lockKey = LOCK_KEY_PREFIX + concertId;
        String lockValue = UUID.randomUUID().toString();
        
        try {
            // Try to acquire lock (non-blocking)
            Boolean lockAcquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
            if (lockAcquired != null && lockAcquired) {
                try {
                    // Critical section: Check and decrement stock atomically
                    String stockKey = STOCK_KEY_PREFIX + concertId;
                    Long stock = redisTemplate.opsForValue().decrement(stockKey);
                    
                    return stock != null && stock >= 0;
                } finally {
                    // Release lock
                    String currentValue = redisTemplate.opsForValue().get(lockKey);
                    if (lockValue.equals(currentValue)) {
                        redisTemplate.delete(lockKey);
                    }
                }
            } else {
                // Lock contention - wait and retry
                Thread.sleep(10);
                return deductStock(concertId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public int getAvailableStock(String concertId) {
        String stockKey = STOCK_KEY_PREFIX + concertId;
        String stock = redisTemplate.opsForValue().get(stockKey);
        return stock != null ? Integer.parseInt(stock) : 0;
    }

    public void initializeConcertStock(String concertId, int totalTickets) {
        String stockKey = STOCK_KEY_PREFIX + concertId;
        redisTemplate.opsForValue().set(stockKey, String.valueOf(totalTickets));
        
        // Create individual tickets in batch
        java.util.List<Ticket> tickets = new java.util.ArrayList<>();
        for (int i = 0; i < totalTickets; i++) {
            Ticket ticket = new Ticket(concertId, generateTicketCode(concertId));
            tickets.add(ticket);
        }
        // Save all tickets in batch
        ticketRepository.saveAll(tickets);
    }

    public int syncStockFromMongoDB(String concertId) {
        // Count actual available tickets in MongoDB
        int actualCount = (int) ticketRepository.findByConcertIdAndStatus(concertId, "AVAILABLE").size();
        
        // Update Redis with actual count
        String stockKey = STOCK_KEY_PREFIX + concertId;
        redisTemplate.opsForValue().set(stockKey, String.valueOf(actualCount));
        
        return actualCount;
    }

    private String generateTicketCode(String concertId) {
        return "TKT-" + concertId.substring(0, 8) + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
