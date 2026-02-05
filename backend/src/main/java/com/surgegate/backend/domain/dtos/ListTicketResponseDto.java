package com.surgegate.backend.domain.dtos;
import com.surgegate.backend.domain.entities.TicketStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTicketResponseDto {
    private String id;
    private TicketStatusEnum status;
    private com.surgegate.backend.domain.dtos.ListTicketTicketTypeResponseDto ticketType;
}