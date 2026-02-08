package com.surgegate.backend.repository;

import com.surgegate.backend.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {
    List<Ticket> findByConcertId(String concertId);
    List<Ticket> findByUserId(String userId);
    List<Ticket> findByConcertIdAndStatus(String concertId, String status);
    Optional<Ticket> findByTicketCode(String ticketCode);
}
