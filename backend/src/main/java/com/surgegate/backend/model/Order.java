package com.surgegate.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String orderId;
    private String eventId;
    private String ticketType;
    private String userId; // User Name or ID
    private String status; // "CONFIRMED"
    private boolean isUsed; // For Staff Validation
}