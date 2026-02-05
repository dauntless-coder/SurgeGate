package com.surgegate.backend.services;

import com.surgegate.backend.domain.entities.QrCode;
import com.surgegate.backend.domain.entities.Ticket;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(String userId, String ticketId);
}