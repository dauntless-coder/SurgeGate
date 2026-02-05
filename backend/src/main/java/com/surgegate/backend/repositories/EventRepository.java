package com.devtiro.tickets.repositories;

import com.devtiro.tickets.domain.entities.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByOrganizerId(String organizerId);
}