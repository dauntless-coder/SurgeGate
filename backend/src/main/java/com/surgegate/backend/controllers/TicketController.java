package com.surgegate.backend.controllers;


import com.surgegate.backend.domain.dtos.GetTicketResponseDto;
import com.surgegate.backend.domain.dtos.ListTicketResponseDto;
import com.surgegate.backend.domain.entities.Event;
import com.surgegate.backend.domain.entities.TicketType;
import com.surgegate.backend.mappers.TicketMapper;
import com.surgegate.backend.services.EventService;
import com.surgegate.backend.services.QrCodeService;
import com.surgegate.backend.services.SurgeGateService;
import com.surgegate.backend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final EventService eventService;
    private final TicketMapper ticketMapper;
    private final QrCodeService qrCodeService;
    private final SurgeGateService surgeGateService;

    // --- ASYNC HIGH-CONCURRENCY PURCHASE ---
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseTicket(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> request
    ) {
        String userId = jwt.getSubject();
        String eventId = request.get("eventId");
        String ticketTypeId = request.get("ticketTypeId");

        // Call the Redis Gatekeeper
        String ticketId = surgeGateService.attemptPurchase(eventId, ticketTypeId, userId);

        if (ticketId != null) {
            // Return 202 Accepted (Processing in background)
            return ResponseEntity.accepted().body(Map.of(
                    "ticketId", ticketId,
                    "status", "PROCESSING",
                    "message", "Order queued successfully."
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Sold Out or Unavailable"));
        }
    }

    @GetMapping
    public Page<ListTicketResponseDto> listTickets(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable
    ) {
        return ticketService.listTicketsForUser(jwt.getSubject(), pageable)
                .map(ticketMapper::toListTicketResponseDto);
    }

    @GetMapping(path = "/{ticketId}")
    public ResponseEntity<GetTicketResponseDto> getTicket(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String ticketId
    ) {
        return ticketService.getTicketForUser(jwt.getSubject(), ticketId)
                .map(ticket -> {
                    // Manually fetch Event details to populate DTO since we are using Mongo
                    Optional<Event> eventOpt = eventService.getPublishedEvent(ticket.getEventId());
                    if(eventOpt.isPresent()) {
                        Event event = eventOpt.get();
                        TicketType type = event.getTicketTypes().stream()
                                .filter(t -> t.getId().equals(ticket.getTicketTypeId()))
                                .findFirst().orElse(null);
                        return ticketMapper.toGetTicketResponseDto(ticket, event, type);
                    }
                    return null;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{ticketId}/qr-codes")
    public ResponseEntity<byte[]> getTicketQrCode(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String ticketId
    ) {
        byte[] qrCodeImage = qrCodeService.getQrCodeImageForUserAndTicket(
                jwt.getSubject(),
                ticketId
        );
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCodeImage);
    }
}