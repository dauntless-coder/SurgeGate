package com.surgegate.backend.controller;

import com.surgegate.backend.model.Concert;
import com.surgegate.backend.model.Order;
import com.surgegate.backend.repository.ConcertRepository;
import com.surgegate.backend.service.InventoryService;
import com.surgegate.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendee")
@CrossOrigin(origins = "http://localhost:3000")
public class AttendeeController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ConcertRepository concertRepository;
    
    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/buy")
    public ResponseEntity<?> buyTicket(
            @RequestParam String concertId,
            @RequestParam String userId) {
        
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (!concert.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Concert not found"));
        }

        Concert c = concert.get();
        
        // Check availability
        int available = inventoryService.getAvailableStock(concertId);
        if (available <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "SOLD_OUT"));
        }

        // Create order (handles concurrency)
        Order order = orderService.createOrder(concertId, userId);
        
        if (order == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create order - tickets may be sold out"));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.getId());
        response.put("ticketId", order.getTicketId());
        response.put("ticketCode", order.getTicketCode());
        response.put("status", "CONFIRMED");
        response.put("price", c.getPrice());
        response.put("message", "✅ Ticket purchased! Share this code with staff: " + order.getTicketCode());

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<?> getUserOrders(@PathVariable String userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        
        // Enrich orders with concert title
        List<Map<String, Object>> enrichedOrders = new java.util.ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", order.getId());
            orderData.put("concertId", order.getConcertId());
            orderData.put("status", order.getStatus());
            orderData.put("amount", order.getAmount());
            orderData.put("ticketCode", order.getTicketCode());
            
            // Fetch concert title
            Optional<Concert> concert = concertRepository.findById(order.getConcertId());
            if (concert.isPresent()) {
                orderData.put("concertTitle", concert.get().getTitle());
            } else {
                orderData.put("concertTitle", "Unknown Event");
            }
            
            enrichedOrders.add(orderData);
        }
        
        return ResponseEntity.ok(enrichedOrders);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrderStatus(@PathVariable String orderId) {
        Order order = orderService.getOrder(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/concerts")
    public ResponseEntity<List<Concert>> getActiveConcerts() {
        List<Concert> concerts = concertRepository.findByStatus("ACTIVE");
        concerts.forEach(c -> c.setAvailableTickets(inventoryService.getAvailableStock(c.getId())));
        return ResponseEntity.ok(concerts);
    }
}
