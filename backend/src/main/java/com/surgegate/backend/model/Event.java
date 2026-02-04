package com.surgegate.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String name;
    private String date;
    private String venue;
    private List<TicketType> ticketTypes; // e.g., VIP, General
}
