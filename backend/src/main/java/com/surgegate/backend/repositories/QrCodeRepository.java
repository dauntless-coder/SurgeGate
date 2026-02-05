package com.surgegate.backend.repositories;

import com.surgegate.backend.domain.entities.QrCode;
import com.surgegate.backend.domain.entities.QrCodeStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends MongoRepository<QrCode, String> {
    // Matches 'ticketPurchaserId' in QrCode Entity
    Optional<QrCode> findByTicketIdAndTicketPurchaserId(String ticketId, String ticketPurchaserId);

    Optional<QrCode> findByIdAndStatus(String id, QrCodeStatusEnum status);
}