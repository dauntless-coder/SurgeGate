package com.surgegate.backend.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "orders")
public class Order {
    // (Generate Getters and Setters here using your IDE)
    @Getter
    @Id
    private String id;
    private String userId;
    private String status; // e.g., "CONFIRMED"
    private LocalDateTime timestamp;

    // Constructors
    public Order() {}
    public Order(String id, String userId, String status) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}