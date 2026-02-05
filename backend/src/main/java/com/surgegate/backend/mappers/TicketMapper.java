package com.surgegate.backend.mappers;


import com.surgegate.backend.domain.dtos.GetTicketResponseDto;
import com.surgegate.backend.domain.dtos.ListTicketResponseDto;
import com.surgegate.backend.domain.dtos.ListTicketTicketTypeResponseDto;
import com.surgegate.backend.entities.Event;
import com.surgegate.backend.entities.Ticket;
import com.surgegate.backend.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    // Maps the Ticket entity to the List DTO.
    // Note: Since Ticket entity doesn't store TicketType name/price,
    // those fields will be null in the list view unless fetched separately.
    @Mapping(target = "ticketType.id", source = "ticketTypeId")
    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

    ListTicketTicketTypeResponseDto toListTicketTicketTypeResponseDto(TicketType ticketType);

    // Complex mapping: Combines Ticket + Event + TicketType into one rich DTO
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