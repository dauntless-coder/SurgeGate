package com.surgegate.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.domain.entities.Ticket;
import com.surgegate.backend.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketOrderConsumer {

    private final TicketRepository ticketRepository;
    private final QrCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "orders_topic", groupId = "ticket-service-group")
    public void processTicketOrder(String message) {
        try {
            // Deserialize the ticket from JSON
            Ticket ticket = objectMapper.readValue(message, Ticket.class);
            
            log.info("Processing ticket order: {}", ticket.getId());
            
            // Save ticket to MongoDB
            Ticket savedTicket = ticketRepository.save(ticket);
            
            // Generate QR code for the ticket
            qrCodeService.generateQrCode(savedTicket);
            
            log.info("Successfully processed ticket order: {}", savedTicket.getId());
            
        } catch (Exception e) {
            log.error("Failed to process ticket order from Kafka", e);
            // In production, you might want to send this to a dead letter queue
            // or implement retry logic
        }
    }
}
