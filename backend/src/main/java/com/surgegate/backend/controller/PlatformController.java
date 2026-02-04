package com.surgegate.backend.controller;

import com.surgegate.backend.kafka.OrderProducer;
import com.surgegate.backend.model.*;
import com.surgegate.backend.repository.*;
import com.surgegate.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Connects to React
public class PlatformController {

    @Autowired private EventRepository eventRepository;
    @Autowired private InventoryService inventoryService;
    @Autowired private OrderProducer orderProducer;
    @Autowired private OrderRepository orderRepository;

    // --- ORGANIZER: Create Event ---
    @PostMapping("/events")
    public Event createEvent(@RequestBody Event event) {
        Event saved = eventRepository.save(event);
        // Initialize Redis Stock for each ticket type
        for (TicketType type : event.getTicketTypes()) {
            inventoryService.initStock(saved.getId(), type.getTypeName(), type.getTotalAllocation());
        }
        return saved;
    }

    // --- ATTENDEE: List Events ---
    @GetMapping("/events")
    public List<Event> getEvents() { return eventRepository.findAll(); }

    // --- ATTENDEE: Buy Ticket ---
    @PostMapping("/buy")
    public ResponseEntity<String> buyTicket(@RequestBody Order request) {
        // 1. High-Speed Redis Check
        boolean success = inventoryService.deductStock(request.getEventId(), request.getTicketType());

        if (success) {
            String orderId = UUID.randomUUID().toString();
            request.setOrderId(orderId);
            request.setStatus("PROCESSING");
            // 2. Async Kafka Process
            orderProducer.sendOrder(request);
            return ResponseEntity.accepted().body(orderId);
        }
        return ResponseEntity.badRequest().body("SOLD_OUT");
    }

    // --- STAFF: Validate Ticket ---
    @PostMapping("/validate/{orderId}")
    public ResponseEntity<String> validate(@PathVariable String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return ResponseEntity.status(404).body("INVALID TICKET");

        Order order = orderOpt.get();
        if (order.isUsed()) return ResponseEntity.status(409).body("ALREADY USED ⚠️");

        order.setUsed(true);
        orderRepository.save(order);
        return ResponseEntity.ok("VALID ✅ - Entry Granted");
    }
}