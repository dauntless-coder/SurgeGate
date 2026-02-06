package com.surgegate.backend.mappers;

import com.surgegate.backend.domain.CreateEventRequest;
import com.surgegate.backend.domain.CreateTicketTypeRequest;
import com.surgegate.backend.domain.UpdateEventRequest;
import com.surgegate.backend.domain.UpdateTicketTypeRequest;
import com.surgegate.backend.domain.dtos.*;
import com.surgegate.backend.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TicketTypeMapper.class})
public interface EventMapper {

    // --- Request Mapping (DTO -> Domain) ---
    CreateEventRequest fromDto(CreateEventRequestDto dto);
    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);
    UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

    // --- Response Mapping (Entity -> DTO) ---

    // Note: TicketTypes list mapping is handled automatically by TicketTypeMapper

    CreateEventResponseDto toDto(Event event);

    UpdateEventResponseDto toUpdateEventResponseDto(Event event);

    ListEventResponseDto toListEventResponseDto(Event event);

    GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);

    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

    // Explicitly ignore fields that don't exist in the 'Public' view DTO to avoid warnings/errors
    // GetPublishedEventDetailsResponseDto only has: id, name, start, end, venue, ticketTypes
    GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);
}