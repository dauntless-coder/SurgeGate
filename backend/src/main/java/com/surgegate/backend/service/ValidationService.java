package com.surgegate.backend.service;

import com.surgegate.backend.model.Order;
import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.repository.OrderRepository;
import com.surgegate.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationService {

    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    public boolean validateTicket(String ticketCode, String staffId) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketCode(ticketCode);
        
        // If not found by ticket code, try to find by ID (MongoDB ID)
        if (!ticketOpt.isPresent()) {
            ticketOpt = ticketRepository.findById(ticketCode);
        }
        
        if (!ticketOpt.isPresent()) {
            return false;
        }

        Ticket ticket = ticketOpt.get();
        
        // Check if already used
        if ("USED".equals(ticket.getStatus())) {
            return false;
        }

        // Check if sold
        if (!"SOLD".equals(ticket.getStatus())) {
            return false;
        }

        // Mark as used
        ticket.setStatus("USED");
        ticket.setValidatedBy(staffId);
        ticketRepository.save(ticket);
        
        return true;
    }

    public boolean validateByOrder(String orderId, String staffId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (!orderOpt.isPresent()) {
            return false;
        }

        Order order = orderOpt.get();
        
        // Check if order is confirmed
        if (!"CONFIRMED".equals(order.getStatus())) {
            return false;
        }

        // Get the ticket and mark as used
        Optional<Ticket> ticketOpt = ticketRepository.findById(order.getTicketId());
        if (!ticketOpt.isPresent()) {
            return false;
        }

        Ticket ticket = ticketOpt.get();
        
        // Check if already used
        if ("USED".equals(ticket.getStatus())) {
            return false;
        }

        // Mark ticket as used
        ticket.setStatus("USED");
        ticket.setValidatedBy(staffId);
        ticketRepository.save(ticket);
        
        // Mark order as validated
        order.setStatus("VALIDATED");
        orderRepository.save(order);
        
        return true;
    }

    public List<Ticket> getValidatedTickets(String concertId) {
        return ticketRepository.findByConcertIdAndStatus(concertId, "USED");
    }

    public Ticket getTicketInfo(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketCode(ticketCode);
        
        // If not found by ticket code, try to find by ID
        if (!ticketOpt.isPresent()) {
            ticketOpt = ticketRepository.findById(ticketCode);
        }
        
        return ticketOpt.orElse(null);
    }
}
