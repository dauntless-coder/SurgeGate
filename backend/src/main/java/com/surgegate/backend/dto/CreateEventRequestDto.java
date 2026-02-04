package com.surgegate.backend.dto;

import com.surgegate.backend.model.Event; // Import your inner TicketType class
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEventRequestDto {
    private String name;
    private String description;
    private LocalDateTime date;
    private String venue;
    private List<Event.TicketType> ticketTypes;
}