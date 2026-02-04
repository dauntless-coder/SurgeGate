package com.surgegate.backend.repository;

import com.surgegate.backend.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    // Spring Boot automatically gives you save(), findAll(), findById(), etc.
    // No extra code needed here!
}