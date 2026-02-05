package com.surgegate.backend.services;

import com.surgegate.backend.domain.entities.TicketValidation;

public interface TicketValidationService {
    TicketValidation validateTicketByQrCode(String qrCodeId);
    TicketValidation validateTicketManually(String ticketId);
}