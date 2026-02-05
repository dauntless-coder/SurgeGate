package com.surgegate.backend.dto;
import com.surgegate.backend.model.Event;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Data public class CreateEventRequest {
    private String name; private String description;
    private LocalDateTime date; private String venue;
    private List<Event.TicketType> ticketTypes;
}