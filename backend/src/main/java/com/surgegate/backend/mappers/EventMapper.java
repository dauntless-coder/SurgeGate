package com.surgegate.backend.mappers;

import com.surgegate.backend.domain.CreateEventRequest;
import com.surgegate.backend.domain.CreateTicketTypeRequest;
import com.surgegate.backend.domain.UpdateEventRequest;
import com.surgegate.backend.domain.UpdateTicketTypeRequest;

import com.surgegate.backend.domain.dtos.*;
import com.surgegate.backend.entities.Event;
import com.surgegate.backend.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // --- Request Mapping (DTO -> Domain) ---
    CreateEventRequest fromDto(CreateEventRequestDto dto);
    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);
    UpdateTicketTypeRequest fromDto(UpdateTicketTypeRequestDto dto);

    // --- Response Mapping (Entity -> DTO) ---

    // Create Response
    CreateEventResponseDto toDto(Event event);
    CreateTicketTypeResponseDto toDto(TicketType ticketType);

    // Update Response
    UpdateEventResponseDto toUpdateEventResponseDto(Event event);
    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

    // List Events
    ListEventResponseDto toListEventResponseDto(Event event);
    ListEventTicketTypeResponseDto toListEventTicketTypeResponseDto(TicketType ticketType);

    // Get Event Details (Organizer)
    GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);
    GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto(TicketType ticketType);

    // List Published Events (Public)
    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

    // Get Published Event Details (Public)
    GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);
    GetPublishedEventDetailsTicketTypesResponseDto toGetPublishedEventDetailsTicketTypesResponseDto(TicketType ticketType);
}