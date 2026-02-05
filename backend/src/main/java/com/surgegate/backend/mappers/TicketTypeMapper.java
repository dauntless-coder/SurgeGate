package com.surgegate.backend.mappers;

import com.surgegate.backend.domain.dtos.CreateTicketTypeResponseDto;
import com.surgegate.backend.domain.dtos.GetEventDetailsTicketTypesResponseDto;
import com.surgegate.backend.domain.dtos.UpdateTicketTypeResponseDto;
import com.surgegate.backend.domain.entities.TicketType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {
    CreateTicketTypeResponseDto toCreateTicketTypeResponseDto(TicketType ticketType);
    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);
    GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto(TicketType ticketType);
}
