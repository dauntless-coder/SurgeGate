package com.surgegate.backend.domain;
import java.math.BigDecimal; // Import BigDecimal
// Remove java.util.UUID
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketTypeRequest {

    private String id; // Changed UUID -> String
    private String name;
    private BigDecimal price; // Changed Double -> BigDecimal
    private String description;
    private Integer totalAvailable;
}