package com.surgegate.backend.controller;

import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:3000")
public class StaffController {

    @Autowired
    private ValidationService validationService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateTicket(
            @RequestParam String orderId,
            @RequestParam String staffId) {
        
        boolean isValid = validationService.validateByOrder(orderId, staffId);
        
        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("status", "VALID");
            response.put("message", "Order validated successfully");
            response.put("orderId", orderId);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "INVALID");
            response.put("message", "Invalid or already used order");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/ticket/{ticketCode}")
    public ResponseEntity<?> getTicketInfo(@PathVariable String ticketCode) {
        Ticket ticket = validationService.getTicketInfo(ticketCode);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/validated/{concertId}")
    public ResponseEntity<List<Ticket>> getValidatedTickets(@PathVariable String concertId) {
        List<Ticket> validatedTickets = validationService.getValidatedTickets(concertId);
        return ResponseEntity.ok(validatedTickets);
    }
}
