package com.surgegate.backend.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    private String id;

    private String name;

    private String description;

    private LocalDateTime start;

    private LocalDateTime end;

    private String venue;

    private LocalDateTime salesStart;

    private LocalDateTime salesEnd;

    private List<com.surgegate.backend.domain.entities.TicketType> ticketTypes;

    private String organizerId;

    @Builder.Default
    private List<com.surgegate.backend.domain.entities.Ticket> tickets = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}