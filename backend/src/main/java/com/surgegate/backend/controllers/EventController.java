package com.surgegate.backend.controllers;

import com.surgegate.backend.dto.CreateEventRequest;
import com.surgegate.backend.entities.Event;
import com.surgegate.backend.repositories.EventRepository;
import com.surgegate.backend.services.SurgeGateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EventController {

    private final EventRepository eventRepository;
    private final SurgeGateService surgeGateService;

    @GetMapping
    public List<Event> getAll() { return eventRepository.findAll(); }

    @PostMapping
    public Event create(@RequestBody CreateEventRequest req) {
        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setName(req.getName());
        event.setDescription(req.getDescription());
        event.setDate(req.getDate());
        event.setVenue(req.getVenue());
        event.setTicketTypes(req.getTicketTypes());
        event.setStatus("PUBLISHED");

        Event saved = eventRepository.save(event);

        // CRITICAL: Initialize Redis Stock
        if(event.getTicketTypes() != null) {
            for(Event.TicketType t : event.getTicketTypes()) {
                surgeGateService.initStock(saved.getId(), t.getId(), t.getQuantity());
            }
        }
        return saved;
    }
}