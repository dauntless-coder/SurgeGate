package com.surgegate.backend.repositories;

import com.surgegate.backend.entities.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    // You can add custom finders here if needed, e.g.:
    // List<Order> findByEventId(String eventId);
}