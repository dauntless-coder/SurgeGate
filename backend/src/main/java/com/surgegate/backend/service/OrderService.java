package com.surgegate.backend.service;

import com.surgegate.backend.model.Concert;
import com.surgegate.backend.model.Order;
import com.surgegate.backend.model.Ticket;
import com.surgegate.backend.repository.ConcertRepository;
import com.surgegate.backend.repository.OrderRepository;
import com.surgegate.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private ConcertRepository concertRepository;

    public Order createOrder(String concertId, String userId) {
        Optional<Concert> concert = concertRepository.findById(concertId);
        if (!concert.isPresent()) {
            return null;
        }

        // Try to deduct stock (handles concurrency with Redis lock)
        if (!inventoryService.deductStock(concertId)) {
            return null; // Stock exhausted
        }

        // Get available ticket
        List<Ticket> availableTickets = ticketRepository.findByConcertIdAndStatus(concertId, "AVAILABLE");
        if (availableTickets.isEmpty()) {
            return null; // No tickets available (shouldn't happen with proper sync)
        }

        Ticket ticket = availableTickets.get(0);
        
        Order order = new Order(concertId, userId, concert.get().getPrice());
        order.setTicketId(ticket.getId());
        order.setTicketCode(ticket.getTicketCode());
        
        // Mark ticket as sold
        ticket.setStatus("SOLD");
        ticket.setUserId(userId);
        ticketRepository.save(ticket);
        
        // Save order
        order = orderRepository.save(order);
        order.setStatus("CONFIRMED");
        
        return orderRepository.save(order);
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getConcertOrders(String concertId) {
        return orderRepository.findByConcertId(concertId);
    }
}
