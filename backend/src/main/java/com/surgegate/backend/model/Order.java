package com.surgegate.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private String concertId;
    private String userId;
    private String ticketId;
    private String ticketCode;
    private double amount;
    private String status; // PENDING, CONFIRMED, FAILED

    // Constructors
    public Order() {}
    
    public Order(String concertId, String userId, double amount) {
        this.id = java.util.UUID.randomUUID().toString();
        this.concertId = concertId;
        this.userId = userId;
        this.amount = amount;
        this.status = "PENDING";
    }

}