package com.surgegate.backend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgegate.backend.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    @Autowired private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired private ObjectMapper objectMapper; // To convert Object to JSON string

    public void sendOrder(Order order) {
        try {
            String json = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("orders_topic", json);
        } catch (Exception e) { e.printStackTrace(); }
    }
}