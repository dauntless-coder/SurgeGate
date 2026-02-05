package com.surgegate.backend.services;

import com.surgegate.backend.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface TicketService {
    Page<Ticket> listTicketsForUser(String userId, Pageable pageable);
    Optional<Ticket> getTicketForUser(String userId, String ticketId);
}