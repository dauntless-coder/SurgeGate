package com.surgegate.backend.domain.dtos;

import com.surgegate.backend.domain.entities.TicketValidationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketValidationResponseDto {
    private String ticketId;
    private TicketValidationStatusEnum status;
}