package com.surgegate.backend.domain.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPublishedEventDetailsResponseDto {

    private String id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;
    private List<com.surgegate.backend.domain.dtos.GetPublishedEventDetailsTicketTypesResponseDto> ticketTypes = new ArrayList<>();
}