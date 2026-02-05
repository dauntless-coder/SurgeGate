package com.surgegate.backend.domain;

import java.math.BigDecimal; // Import BigDecimal
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTicketTypeRequest {

    private String name;
    private BigDecimal price; // Changed Double -> BigDecimal
    private String description;
    private Integer totalAvailable;
}