package com.surgegate.backend.repositories;

import com.surgegate.backend.entities.TicketValidation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketValidationRepository extends MongoRepository<TicketValidation, String> {
}