package com.surgegate.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {

    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "orders_topic", groupId = "surgegate-group")
    public void processOrder(String message) {
        try {
            Ticket ticket = objectMapper.readValue(message, Ticket.class);
            ticket.setStatus("CONFIRMED");
            ticket.setQrCode("SG-" + ticket.getId().substring(0, 8)); // Generate Simple QR string
            ticketRepository.save(ticket);
            System.out.println("✅ Ticket Confirmed: " + ticket.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}