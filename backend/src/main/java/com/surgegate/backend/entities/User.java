package com.devtiro.tickets.domain.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
public class User {
    @Id
    private String id; // This will hold the Auth0 ID (e.g., "auth0|12345")
    private String email;
    private String name;
}