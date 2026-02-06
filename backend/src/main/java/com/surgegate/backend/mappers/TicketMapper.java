package com.surgegate.backend.mappers;

import com.surgegate.backend.domain.dtos.GetTicketResponseDto;
import com.surgegate.backend.domain.dtos.ListTicketResponseDto;
import com.surgegate.backend.domain.dtos.ListTicketTicketTypeResponseDto;
import com.surgegate.backend.domain.entities.Event;
import com.surgegate.backend.domain.entities.Ticket;
import com.surgegate.backend.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(target = "ticketType.id", source = "ticketTypeId")
    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

    ListTicketTicketTypeResponseDto toListTicketTicketTypeResponseDto(TicketType ticketType);

    @Mapping(target = "id", source = "ticket.id")
    @Mapping(target = "status", source = "ticket.status")
    @Mapping(target = "price", source = "ticketType.price")
    @Mapping(target = "description", source = "ticketType.description")
    @Mapping(target = "eventName", source = "event.name")
    @Mapping(target = "eventVenue", source = "event.venue")
    @Mapping(target = "eventStart", source = "event.start")
    @Mapping(target = "eventEnd", source = "event.end")
    GetTicketResponseDto toGetTicketResponseDto(Ticket ticket, Event event, TicketType ticketType);
}