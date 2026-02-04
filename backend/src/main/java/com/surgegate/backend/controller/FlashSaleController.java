package com.surgegate.backend.controller;

import com.surgegate.backend.kafka.OrderProducer;
import com.surgegate.backend.model.Order;
import com.surgegate.backend.repository.OrderRepository;
import com.surgegate.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Allow React to talk to Java
public class FlashSaleController {

    @Autowired private InventoryService inventoryService;
    @Autowired private OrderProducer orderProducer;
    @Autowired private OrderRepository orderRepository;

    @PostMapping("/buy")
    public ResponseEntity<String> buyTicket(@RequestParam String userId) {
        // 1. Check Redis Stock
        if (inventoryService.deductStock("concert_123")) {
            String orderId = UUID.randomUUID().toString();

            // 2. Send to Kafka (Async processing)
            String message = orderId + "," + userId;
            orderProducer.sendOrder(message);

            return ResponseEntity.accepted().body(orderId);
        } else {
            return ResponseEntity.badRequest().body("SOLD_OUT");
        }
    }

    // Polling Endpoint
    @GetMapping("/status/{orderId}")
    public ResponseEntity<String> checkStatus(@PathVariable String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()) return ResponseEntity.ok("CONFIRMED");
        return ResponseEntity.ok("PENDING");
    }
}