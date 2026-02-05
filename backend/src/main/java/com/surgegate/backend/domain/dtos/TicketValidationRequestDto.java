package com.surgegate.backend.domain.dtos;

import com.surgegate.backend.domain.entities.TicketValidationMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationRequestDto {
    private String id;
    private TicketValidationMethod method;
}