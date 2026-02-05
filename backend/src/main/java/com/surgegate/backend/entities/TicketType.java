package com.surgegate.backend.entities;

import lombok.Data;

@Data
public class TicketType {
    private String typeName; // "VIP"
    private double price;
    private int totalAllocation;
}
