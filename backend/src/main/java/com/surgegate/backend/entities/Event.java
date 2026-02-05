package com.devtiro.tickets.domain.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document(collection = "events")
public class Event {
    @Id
    private String id;

    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;
    private EventStatusEnum status;

    // Store simple User ID string instead of JoinColumn
    private String organizerId;

    // Embed Ticket Types directly for performance
    private List<TicketType> ticketTypes;

    @Data
    @Builder
    public static class TicketType {
        private String id;
        private String name;
        private double price;
        private int quantity; // Total Allocation
    }
}