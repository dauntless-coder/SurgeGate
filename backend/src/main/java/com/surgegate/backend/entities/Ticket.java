package com.devtiro.tickets.domain.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "tickets")
public class Ticket {
    @Id
    private String id;
    private String eventId;
    private String ticketTypeId;
    private String userId; // Auth0 User ID
    private TicketStatusEnum status;
    private String qrCode;
    private LocalDateTime createdDate;
}