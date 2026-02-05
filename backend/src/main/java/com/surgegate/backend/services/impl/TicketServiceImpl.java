package com.surgegate.backend.services.impl;

import com.surgegate.backend.domain.entities.Ticket;
import com.surgegate.backend.repositories.TicketRepository;
import com.surgegate.backend.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Page<Ticket> listTicketsForUser(String userId, Pageable pageable) {
        // Note: Ensure Repository method is findByUserId, not findByPurchaserId
        return ticketRepository.findByUserId(userId, pageable);
    }

    @Override
    public Optional<Ticket> getTicketForUser(String userId, String ticketId) {
        return ticketRepository.findByIdAndUserId(ticketId, userId);
    }
}