package com.surgegate.backend;

import com.fasterxml.jackson.databind.ObjectMapper; // <--- Import 1
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // <--- Import 2
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    // --- ADD THIS BLOCK HERE ---
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
