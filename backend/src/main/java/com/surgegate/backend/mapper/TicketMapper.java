package com.surgegate.backend.mapper;

import com.surgegate.backend.dto.TicketResponseDto;
import com.surgegate.backend.model.Event;
import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final EventRepository eventRepository;

    public TicketResponseDto toDto(Ticket ticket) {
        // Fetch event name for the ticket
        String eventName = eventRepository.findById(ticket.getEventId())
                .map(Event::getName)
                .orElse("Unknown Event");

        return TicketResponseDto.builder()
                .ticketId(ticket.getId())
                .eventName(eventName)
                .ticketType(ticket.getTicketTypeId())
                .status(ticket.getStatus())
                .qrCode(ticket.getQrCode())
                .build();
    }
}