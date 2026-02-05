package com.surgegate.backend.mappers;

import com.surgegate.backend.dto.TicketResponse;
import com.surgegate.backend.entities.Event;
import com.surgegate.backend.entities.Ticket;
import com.surgegate.backend.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final EventRepository eventRepository;

    public TicketResponse toDto(Ticket ticket) {
        // Fetch event name for the ticket
        String eventName = eventRepository.findById(ticket.getEventId())
                .map(Event::getName)
                .orElse("Unknown Event");

        return TicketResponse.builder()
                .ticketId(ticket.getId())
                .eventName(eventName)
                .ticketType(ticket.getTicketTypeId())
                .status(ticket.getStatus())
                .qrCode(ticket.getQrCode())
                .build();
    }
}