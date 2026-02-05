package com.surgegate.backend.controller;

import com.surgegate.backend.dto.TicketResponse;
import com.surgegate.backend.model.User;
import com.surgegate.backend.repository.EventRepository;
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
@CrossOrigin("*")
public class TicketController {

    private final SurgeGateService surgeGateService;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestBody Map<String, String> req, @AuthenticationPrincipal User user) {
        String ticketId = surgeGateService.attemptPurchase(
                req.get("eventId"), req.get("ticketTypeId"), user.getId()
        );

        if (ticketId != null) {
            return ResponseEntity.accepted().body(Map.of("id", ticketId, "status", "PROCESSING"));
        }
        return ResponseEntity.status(400).body("SOLD OUT");
    }

    @GetMapping("/mine")
    public List<TicketResponse> myTickets(@AuthenticationPrincipal User user) {
        return ticketRepository.findByUserId(user.getId()).stream()
                .map(t -> TicketResponse.builder()
                        .ticketId(t.getId())
                        .eventName(eventRepository.findById(t.getEventId()).map(e -> e.getName()).orElse("Unknown"))
                        .status(t.getStatus())
                        .qrCode(t.getQrCode())
                        .build())
                .toList();
    }
}