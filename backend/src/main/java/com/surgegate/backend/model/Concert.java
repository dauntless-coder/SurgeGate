package com.surgegate.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "concerts")
public class Concert {
    
    @Id
    private String id;
    private String organizerId;
    private String title;
    private String description;
    private String venue;
    private int totalTickets;
    private int availableTickets;
    private double price;
    private String status; // DRAFT, ACTIVE, CANCELLED, ENDED
    
    public Concert() {
        this.id = UUID.randomUUID().toString();
    }
    
    public Concert(String organizerId, String title, String venue, int totalTickets, double price) {
        this.id = UUID.randomUUID().toString();
        this.organizerId = organizerId;
        this.title = title;
        this.venue = venue;
        this.totalTickets = totalTickets;
        this.availableTickets = totalTickets;
        this.price = price;
        this.status = "DRAFT";
    }
}
