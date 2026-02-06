package com.surgegate.backend.domain.entities;

public enum TicketStatusEnum {
    PURCHASED,
    CANCELLED,
    PENDING,
    // Added to resolve Mapper errors:
    CONFIRMED,
    USED,
    EXPIRED,
    REFUNDED
}