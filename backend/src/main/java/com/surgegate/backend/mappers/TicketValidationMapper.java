package com.surgegate.backend.mappers;


import com.surgegate.backend.domain.dtos.TicketValidationResponseDto;
import com.surgegate.backend.entities.TicketValidation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketValidationMapper {

    @Mapping(target = "ticketId", source = "ticketId")
    @Mapping(target = "status", source = "status")
    TicketValidationResponseDto toTicketValidationResponseDto(TicketValidation ticketValidation);
}