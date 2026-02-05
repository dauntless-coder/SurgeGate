package com.surgegate.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "events")
public class Event {
    @Id private String id;
    private String name;
    private String description;
    private LocalDateTime date;
    private String venue;
    private String status; // PUBLISHED, DRAFT
    private List<TicketType> ticketTypes;

    @Data
    public static class TicketType {
        private String id;
        private String name;
        private double price;
        private int quantity; // Total stock
    }
}