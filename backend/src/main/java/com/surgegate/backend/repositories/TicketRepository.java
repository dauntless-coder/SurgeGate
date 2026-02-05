package com.surgegate.backend.repositories;

import com.surgegate.backend.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    int countByTicketTypeId(String ticketTypeId);

    // Matches the 'userId' field in the Ticket Entity
    Page<Ticket> findByUserId(String userId, Pageable pageable);

    Optional<Ticket> findByIdAndUserId(String id, String userId);
}