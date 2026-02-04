package com.surgegate.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketValidationResponseDto {
    private boolean valid;
    private String message;
    private String attendeeName;
    private String eventName;
    private String ticketType;
}