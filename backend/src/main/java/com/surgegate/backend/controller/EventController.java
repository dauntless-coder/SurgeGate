package com.surgegate.backend.controller;

import com.surgegate.backend.dto.CreateEventRequestDto;
import com.surgegate.backend.mapper.EventMapper;
import com.surgegate.backend.model.Event;
import com.surgegate.backend.repository.EventRepository;
import com.surgegate.backend.service.SurgeGateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EventController {

    private final EventRepository eventRepository;
    private final SurgeGateService surgeGateService; // To init stock
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequestDto request) {
        // 1. Convert DTO to Entity
        Event event = eventMapper.toEntity(request);

        // 2. Save to Mongo
        Event savedEvent = eventRepository.save(event);

        // 3. Initialize Redis Stock (The SurgeGate Logic)
        if (event.getTicketTypes() != null) {
            for (Event.TicketType type : event.getTicketTypes()) {
                surgeGateService.initStock(savedEvent.getId(), type.getName(), type.getQuantity());
            }
        }

        return ResponseEntity.ok(savedEvent);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
