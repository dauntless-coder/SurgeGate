package com.surgegate.backend.repository;

import com.surgegate.backend.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
    // This allows finding all tickets for a specific user (for their dashboard)
    List<Ticket> findByUserId(String userId);
}