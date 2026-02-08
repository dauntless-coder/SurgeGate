package com.surgegate.backend.kafka;


import com.surgegate.backend.model.Order;
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
        // Message format: "orderId,userId,concertId,price"
        String[] parts = message.split(",");
        if (parts.length >= 4) {
            Order order = new Order(parts[2], parts[1], Double.parseDouble(parts[3]));
            order.setId(parts[0]);
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
            System.out.println("Order Processed & Saved: " + parts[0]);
        }
    }
}