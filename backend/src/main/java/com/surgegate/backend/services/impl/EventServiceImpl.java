package com.surgegate.backend.services.impl;

import com.surgegate.backend.domain.CreateEventRequest;
import com.surgegate.backend.domain.UpdateEventRequest;
import com.surgegate.backend.domain.UpdateTicketTypeRequest;
import com.surgegate.backend.domain.entities.Event;
import com.surgegate.backend.domain.entities.EventStatusEnum;
import com.surgegate.backend.domain.entities.TicketType;
import com.surgegate.backend.domain.entities.User;
import com.surgegate.backend.exceptions.EventNotFoundException;
import com.surgegate.backend.exceptions.EventUpdateException;
import com.surgegate.backend.exceptions.TicketTypeNotFoundException;
import com.surgegate.backend.exceptions.UserNotFoundException;
import com.surgegate.backend.repositories.EventRepository;
import com.surgegate.backend.repositories.UserRepository;
import com.surgegate.backend.services.EventService;
import com.surgegate.backend.services.SurgeGateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SurgeGateService surgeGateService; // Inject Redis logic

    @Override
    @Transactional
    public Event createEvent(String organizerId, CreateEventRequest event) {
        // 1. Validate Organizer
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with ID '%s' not found", organizerId))
                );

        Event eventToCreate = new Event();
        String eventId = UUID.randomUUID().toString(); // Pre-generate ID
        eventToCreate.setId(eventId);

        // 2. Map Ticket Types & Generate IDs
        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(
                req -> {
                    TicketType tt = new TicketType();
                    tt.setId(UUID.randomUUID().toString()); // CRITICAL: Generate ID for Redis Key
                    tt.setName(req.getName());
                    tt.setPrice(req.getPrice());
                    tt.setDescription(req.getDescription());
                    tt.setTotalAvailable(req.getTotalAvailable());
                    return tt;
                }).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStart(event.getStart());
        eventToCreate.setEnd(event.getEnd());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSalesStart(event.getSalesStart());
        eventToCreate.setSalesEnd(event.getSalesEnd());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setOrganizerId(organizer.getId());
        eventToCreate.setTicketTypes(ticketTypesToCreate);

        Event savedEvent = eventRepository.save(eventToCreate);

        // 3. CRITICAL: Initialize Stock in Redis for the Gatekeeper
        ticketTypesToCreate.forEach(tt ->
                surgeGateService.initStock(savedEvent.getId(), tt.getId(), tt.getTotalAvailable())
        );

        return savedEvent;
    }

    @Override
    public Page<Event> listEventsForOrganizer(String organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId, pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(String organizerId, String id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(String organizerId, String id, UpdateEventRequest event) {
        Event existingEvent = eventRepository.findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        // Update basic fields
        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());

        // Update Ticket Types
        Map<String, TicketType> existingTypes = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        List<TicketType> updatedTypes = new ArrayList<>();

        for (UpdateTicketTypeRequest req : event.getTicketTypes()) {
            if (req.getId() != null && existingTypes.containsKey(req.getId())) {
                // Update existing
                TicketType tt = existingTypes.get(req.getId());
                tt.setName(req.getName());
                tt.setPrice(req.getPrice());
                tt.setDescription(req.getDescription());
                tt.setTotalAvailable(req.getTotalAvailable());
                updatedTypes.add(tt);

                // Re-sync Redis Stock (Optional: logic depends on if you want to reset stock)
                surgeGateService.initStock(id, tt.getId(), req.getTotalAvailable());
            } else {
                // Create new
                TicketType tt = new TicketType();
                tt.setId(UUID.randomUUID().toString());
                tt.setName(req.getName());
                tt.setPrice(req.getPrice());
                tt.setDescription(req.getDescription());
                tt.setTotalAvailable(req.getTotalAvailable());
                updatedTypes.add(tt);

                // Init Redis Stock
                surgeGateService.initStock(id, tt.getId(), tt.getTotalAvailable());
            }
        }

        existingEvent.setTicketTypes(updatedTypes);
        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEventForOrganizer(String organizerId, String id) {
        // Fetch event to get ticket types before deletion
        Event event = eventRepository.findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        
        // Clean up Redis stock keys for all ticket types
        event.getTicketTypes().forEach(tt -> {
            String key = "stock:" + id + ":" + tt.getId();
            surgeGateService.deleteStock(key);
        });
        
        eventRepository.deleteById(id);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvent(String id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }
}
