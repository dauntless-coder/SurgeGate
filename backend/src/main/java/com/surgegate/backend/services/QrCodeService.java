package com.surgegate.backend.services;

import com.surgegate.backend.entities.QrCode;
import com.surgegate.backend.entities.Ticket;

public interface QrCodeService {

    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(String userId, String ticketId);
}