package com.surgegate.backend.mappers;

import com.surgegate.backend.domain.CreateEventRequest;
import com.surgegate.backend.domain.UpdateEventRequest;
import com.surgegate.backend.domain.dtos.*;
import com.surgegate.backend.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TicketTypeMapper.class})
public interface EventMapper {

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    UpdateEventRequest fromDto(UpdateEventRequestDto dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CreateEventResponseDto toDto(Event event);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UpdateEventResponseDto toUpdateEventResponseDto(Event event);

    ListEventResponseDto toListEventResponseDto(Event event);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GetEventDetailsResponseDto toGetEventDetailsResponseDto(Event event);

    ListPublishedEventResponseDto toListPublishedEventResponseDto(Event event);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GetPublishedEventDetailsResponseDto toGetPublishedEventDetailsResponseDto(Event event);
}
