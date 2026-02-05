package com.surgegate.backend.domain.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListEventTicketTypeResponseDto {
    private String id;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer totalAvailable;
}