package com.surgegate.backend.model;

import lombok.Data;

@Data
public class TicketType {
    private String typeName; // "VIP"
    private double price;
    private int totalAllocation;
}
