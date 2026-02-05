package com.surgegate.backend.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder public class TicketResponse {
    private String ticketId; private String eventName;
    private String ticketType; private String status; private String qrCode;
}