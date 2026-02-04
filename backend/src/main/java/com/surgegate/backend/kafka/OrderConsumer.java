package com.surgegate.backend.kafka;

import com.surgegate.backend.model.Order;
import com.surgegate.backend.repository.OrderRepository;
import com.surgegate.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "orders_topic", groupId = "order-group")
    public void consumeOrder(String message) {
        // Message format: "orderId,userId"
        String[] parts = message.split(",");
        Order order = new Order(parts[0], parts[1], "CONFIRMED");
        orderRepository.save(order);
        System.out.println("Order Processed & Saved: " + parts[0]);
    }
}