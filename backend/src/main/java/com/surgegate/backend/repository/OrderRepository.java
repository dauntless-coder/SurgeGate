package com.surgegate.backend.repository;

import com.surgegate.backend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    // You can add custom finders here if needed, e.g.:
    // List<Order> findByEventId(String eventId);
}