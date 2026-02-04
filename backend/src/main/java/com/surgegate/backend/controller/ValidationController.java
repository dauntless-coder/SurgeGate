package com.surgegate.backend.controller;

import com.surgegate.backend.dto.TicketValidationRequestDto;
import com.surgegate.backend.dto.TicketValidationResponseDto;
import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ValidationController {

    private final TicketRepository ticketRepository;

    @PostMapping("/validate")
    public ResponseEntity<TicketValidationResponseDto> validate(@RequestBody TicketValidationRequestDto request) {
        // Find ticket by the ID from QR code
        Optional<Ticket> ticketOpt = ticketRepository.findById(request.getTicketId());

        if (ticketOpt.isEmpty()) {
            return ResponseEntity.status(404).body(TicketValidationResponseDto.builder()
                    .valid(false)
                    .message("Ticket Not Found")
                    .build());
        }

        Ticket ticket = ticketOpt.get();

        if ("USED".equals(ticket.getStatus())) {
            return ResponseEntity.status(409).body(TicketValidationResponseDto.builder()
                    .valid(false)
                    .message("ALREADY USED on " + ticket.getQrCode()) // Assuming qrCode field stores timestamp or similar
                    .build());
        }

        // Mark as used
        ticket.setStatus("USED");
        ticketRepository.save(ticket);

        return ResponseEntity.ok(TicketValidationResponseDto.builder()
                .valid(true)
                .message("Entry Granted")
                .attendeeName(ticket.getUserId()) // or lookup user name
                .eventName(ticket.getEventId())   // or lookup event name
                .ticketType(ticket.getTicketTypeId())
                .build());
    }
}