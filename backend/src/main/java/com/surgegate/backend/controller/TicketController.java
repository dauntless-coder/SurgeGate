package com.surgegate.backend.controller;

import com.surgegate.backend.dto.TicketResponseDto;
import com.surgegate.backend.mapper.TicketMapper;
import com.surgegate.backend.model.User;
import com.surgegate.backend.repository.TicketRepository;
import com.surgegate.backend.service.SurgeGateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin("*") // Allows React Frontend to access this
public class TicketController {

    private final SurgeGateService surgeGateService; // The Engine (Redis/Kafka)
    private final TicketRepository ticketRepository; // The Database (Mongo)
    private final TicketMapper ticketMapper;         // The Translator (Entity -> DTO)

    // --- 1. BUY TICKET (High-Speed Engine) ---
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTicket(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User user
    ) {
        // 1. Extract Data
        String eventId = request.get("eventId");
        String ticketTypeId = request.get("ticketTypeId");

        // 2. Attempt Atomic Purchase (Redis Checks Stock -> Kafka Queues Order)
        String ticketId = surgeGateService.attemptPurchase(eventId, ticketTypeId, user.getId());

        // 3. Respond immediately (Async processing happens in background)
        if (ticketId != null) {
            return ResponseEntity.accepted().body(Map.of(
                    "id", ticketId,
                    "status", "PROCESSING",
                    "message", "Order received! Check 'My Tickets' in a moment."
            ));
        } else {
            return ResponseEntity.status(400).body("SOLD OUT");
        }
    }

    // --- 2. LIST MY TICKETS (User Dashboard) ---
    @GetMapping("/mine")
    public ResponseEntity<List<TicketResponseDto>> getMyTickets(@AuthenticationPrincipal User user) {
        // Fetch from Mongo -> Convert to DTO -> Return JSON
        List<TicketResponseDto> tickets = ticketRepository.findByUserId(user.getId())
                .stream()
                .map(ticketMapper::toDto)
                .toList();

        return ResponseEntity.ok(tickets);
    }

    // --- 3. GET SINGLE TICKET DETAILS ---
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponseDto> getTicket(
            @PathVariable String ticketId,
            @AuthenticationPrincipal User user
    ) {
        return ticketRepository.findById(ticketId)
                // Security Check: Ensure the ticket belongs to the logged-in user!
                .filter(ticket -> ticket.getUserId().equals(user.getId()))
                .map(ticketMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}