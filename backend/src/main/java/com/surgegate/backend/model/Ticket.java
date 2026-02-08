package com.surgegate.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "tickets")
public class Ticket {
    
    @Id
    private String id;
    private String concertId;
    private String userId;
    private String ticketCode;
    private String status; // AVAILABLE, SOLD, USED, CANCELLED
    private String validatedBy; // staff ID
    
    public Ticket() {}
    
    public Ticket(String concertId, String ticketCode) {
        this.concertId = concertId;
        this.ticketCode = ticketCode;
        this.status = "AVAILABLE";
    }
}
