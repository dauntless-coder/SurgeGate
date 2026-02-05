package com.surgegate.backend.services.impl;

import com.surgegate.backend.domain.entities.QrCode;
import com.surgegate.backend.domain.entities.QrCodeStatusEnum;
import com.surgegate.backend.domain.entities.Ticket;
import com.surgegate.backend.domain.entities.TicketValidation;
import com.surgegate.backend.domain.entities.TicketValidationMethod;
import com.surgegate.backend.domain.entities.TicketValidationStatusEnum;
import com.surgegate.backend.exceptions.QrCodeNotFoundException;
import com.surgegate.backend.exceptions.TicketNotFoundException;
import com.surgegate.backend.repositories.QrCodeRepository;
import com.surgegate.backend.repositories.TicketRepository;
import com.surgegate.backend.repositories.TicketValidationRepository;
import com.surgegate.backend.services.TicketValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketValidationServiceImpl implements TicketValidationService {

    private final QrCodeRepository qrCodeRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final TicketRepository ticketRepository;

    @Override
    public TicketValidation validateTicketByQrCode(String qrCodeId) {
        QrCode qrCode = qrCodeRepository.findByIdAndStatus(qrCodeId, QrCodeStatusEnum.ACTIVE)
                .orElseThrow(() -> new QrCodeNotFoundException(
                        String.format("QR Code with ID %s was not found", qrCodeId)
                ));

        // We must fetch the Ticket manually since Mongo doesn't do lazy loading relationships automatically
        Ticket ticket = ticketRepository.findById(qrCode.getTicketId())
                .orElseThrow(TicketNotFoundException::new);

        return validateTicket(ticket, TicketValidationMethod.QR_SCAN);
    }

    @Override
    public TicketValidation validateTicketManually(String ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);
        return validateTicket(ticket, TicketValidationMethod.MANUAL);
    }

    private TicketValidation validateTicket(Ticket ticket, TicketValidationMethod method) {
        TicketValidation validation = new TicketValidation();
        validation.setTicketId(ticket.getId());
        validation.setValidationMethod(method);
        validation.setCreatedAt(LocalDateTime.now());

        // Simple validation logic: If any previous validation was VALID, this one is INVALID
        // Note: This requires querying previous validations if you store them separately
        // For now, we assume valid:
        validation.setStatus(TicketValidationStatusEnum.VALID);

        return ticketValidationRepository.save(validation);
    }
}
