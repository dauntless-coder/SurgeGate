package com.surgegate.backend.mappers;

import com.surgegate.backend.domain.dtos.CreateTicketTypeResponseDto;
import com.surgegate.backend.domain.dtos.GetEventDetailsTicketTypesResponseDto;
import com.surgegate.backend.domain.dtos.UpdateTicketTypeResponseDto;
import com.surgegate.backend.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CreateTicketTypeResponseDto toCreateTicketTypeResponseDto(TicketType ticketType);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UpdateTicketTypeResponseDto toUpdateTicketTypeResponseDto(TicketType ticketType);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GetEventDetailsTicketTypesResponseDto toGetEventDetailsTicketTypesResponseDto(TicketType ticketType);
}
