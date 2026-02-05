package com.surgegate.backend.domain.dtos;

import com.surgegate.backend.domain.entities.TicketStatusEnum;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTicketResponseDto {
    private String id;
    private TicketStatusEnum status;
    private BigDecimal price;
    private String description;
    private String eventName;
    private String eventVenue;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
}