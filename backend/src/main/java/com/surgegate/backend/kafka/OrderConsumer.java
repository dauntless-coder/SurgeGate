package com.surgegate.backend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.model.Order;
import com.surgegate.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    @Autowired private OrderRepository orderRepository;
    @Autowired private ObjectMapper objectMapper;

    @KafkaListener(topics = "orders_topic", groupId = "order-group")
    public void consume(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            order.setStatus("CONFIRMED");
            order.setUsed(false);
            orderRepository.save(order);
            System.out.println("✅ TICKET CREATED: " + order.getOrderId());
        } catch (Exception e) { e.printStackTrace(); }
    }
}