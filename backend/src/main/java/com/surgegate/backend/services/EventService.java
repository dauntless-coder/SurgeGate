package com.surgegate.backend.services;

import com.surgegate.backend.domain.CreateEventRequest;
import com.surgegate.backend.domain.UpdateEventRequest;
import com.surgegate.backend.domain.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventService {

    Event createEvent(String organizerId, CreateEventRequest event);

    Page<Event> listEventsForOrganizer(String organizerId, Pageable pageable);

    Optional<Event> getEventForOrganizer(String organizerId, String id);

    Event updateEventForOrganizer(String organizerId, String id, UpdateEventRequest event);

    void deleteEventForOrganizer(String organizerId, String id);

    Page<Event> listPublishedEvents(Pageable pageable);

    Page<Event> searchPublishedEvents(String query, Pageable pageable);

    Optional<Event> getPublishedEvent(String id);
}