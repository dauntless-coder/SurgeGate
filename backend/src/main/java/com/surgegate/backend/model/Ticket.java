package com.surgegate.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;
    private String eventId;
    private String ticketTypeId;
    private String userId;
    private String status; // "PENDING", "CONFIRMED", "USED"
    private String qrCode; // Created later by the consumer
}