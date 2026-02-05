package com.surgegate.backend.mappers;

import com.surgegate.backend.dto.CreateEventRequestDto;
import com.surgegate.backend.entities.Event;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventMapper {

    public Event toEntity(CreateEventRequestDto dto) {
        Event event = new Event();
        event.setId(UUID.randomUUID().toString()); // Generate ID here
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setDate(dto.getDate());
        event.setVenue(dto.getVenue());
        event.setTicketTypes(dto.getTicketTypes());
        return event;
    }
}