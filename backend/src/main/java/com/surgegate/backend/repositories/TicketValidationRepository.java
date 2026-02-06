package com.surgegate.backend.repositories;

import com.surgegate.backend.domain.entities.TicketValidation;
import com.surgegate.backend.domain.entities.TicketValidationStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketValidationRepository extends MongoRepository<TicketValidation, String> {
    
    // Find all validations for a specific ticket
    List<TicketValidation> findByTicketId(String ticketId);
    
    // Find validations by ticket ID and status
    Optional<TicketValidation> findByTicketIdAndStatus(String ticketId, TicketValidationStatusEnum status);
}
