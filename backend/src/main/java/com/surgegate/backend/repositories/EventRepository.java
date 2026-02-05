package com.surgegate.backend.repositories;

import com.surgegate.backend.entities.Event;
import com.surgegate.backend.entities.EventStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    Page<Event> findByOrganizerId(String organizerId, Pageable pageable);

    Optional<Event> findByIdAndOrganizerId(String id, String organizerId);

    Page<Event> findByStatus(EventStatusEnum status, Pageable pageable);

    // MongoDB Native Text Search
    // Requires a text index on 'name' and 'venue' in MongoDB:
    // db.events.createIndex({ name: "text", venue: "text" })
    @Query("{ '$text': { '$search': ?0 } }")
    Page<Event> searchEvents(String searchTerm, Pageable pageable);

    Optional<Event> findByIdAndStatus(String id, EventStatusEnum status);
}