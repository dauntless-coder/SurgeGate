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
@CrossOrigin("*") // Allows React Frontend to access this
public class EventController {

    private final EventRepository eventRepository;
    private final SurgeGateService surgeGateService; // The Redis Logic
    private final EventMapper eventMapper;           // DTO <-> Entity Converter

    // --- 1. PUBLIC: LIST ALL EVENTS (Landing Page) ---
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // --- 2. PUBLIC: GET SINGLE EVENT (Purchase Page) ---
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable String id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 3. ADMIN/ORGANIZER: CREATE EVENT (Dashboard) ---
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventRequestDto request) {
        // A. Convert DTO to Database Entity
        Event event = eventMapper.toEntity(request);

        // B. Save Event to MongoDB
        Event savedEvent = eventRepository.save(event);

        // C. CRITICAL: Initialize Redis Stock for SurgeGate Engine
        // This prevents the "Thundering Herd" by loading stock into memory immediately.
        if (event.getTicketTypes() != null) {
            for (Event.TicketType type : event.getTicketTypes()) {
                // Key format: stock:{eventId}:{ticketTypeId}
                surgeGateService.initStock(savedEvent.getId(), type.getId(), type.getQuantity());
            }
        }

        return ResponseEntity.ok(savedEvent);
    }

    // --- 4. ADMIN: UPDATE EVENT ---
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody CreateEventRequestDto request) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    // Update fields
                    existingEvent.setName(request.getName());
                    existingEvent.setDescription(request.getDescription());
                    existingEvent.setDate(request.getDate());
                    existingEvent.setVenue(request.getVenue());

                    // Note: Updating Ticket Quantities here requires complex Redis logic
                    // (handling race conditions if sale is live).
                    // For this version, we update DB metadata only.

                    return ResponseEntity.ok(eventRepository.save(existingEvent));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 5. ADMIN: DELETE EVENT ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            // Optional: You could also delete the Redis keys here to clean up
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}