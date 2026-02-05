package com.surgegate.backend.controllers;


import com.surgegate.backend.domain.dtos.*;
import com.surgegate.backend.entities.Event;
import com.surgegate.backend.mappers.EventMapper;
import com.surgegate.backend.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<CreateEventResponseDto> createEvent(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {

        String userId = jwt.getSubject();
        // Use the Mapper to convert DTO -> Entity (or specialized Request object)
        // Assuming your Mapper handles CreateEventRequestDto -> CreateEventRequest (domain)
        // For simplicity in this stack, we often map DTO -> Request Object inside Service or here.
        // Let's assume the Service takes the DTO or a mapped object.
        // If Service takes 'CreateEventRequest', ensure Mapper exists.

        // Correct flow based on your Service definition:
        com.surgegate.backend.domain.CreateEventRequest domainReq = eventMapper.fromDto(createEventRequestDto);
        Event createdEvent = eventService.createEvent(userId, domainReq);

        CreateEventResponseDto response = eventMapper.toDto(createdEvent);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{eventId}")
    public ResponseEntity<UpdateEventResponseDto> updateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String eventId,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {

        String userId = jwt.getSubject();
        com.surgegate.backend.domain.UpdateEventRequest domainReq = eventMapper.fromDto(updateEventRequestDto);

        Event updatedEvent = eventService.updateEventForOrganizer(userId, eventId, domainReq);
        return ResponseEntity.ok(eventMapper.toUpdateEventResponseDto(updatedEvent));
    }

    @GetMapping
    public ResponseEntity<Page<ListEventResponseDto>> listEvents(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {

        String userId = jwt.getSubject();
        Page<Event> events = eventService.listEventsForOrganizer(userId, pageable);
        return ResponseEntity.ok(events.map(eventMapper::toListEventResponseDto));
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<GetEventDetailsResponseDto> getEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String eventId) {

        String userId = jwt.getSubject();
        return eventService.getEventForOrganizer(userId, eventId)
                .map(eventMapper::toGetEventDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String eventId) {

        String userId = jwt.getSubject();
        eventService.deleteEventForOrganizer(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}