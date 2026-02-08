package com.surgegate.backend.repository;

import com.surgegate.backend.model.Concert;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConcertRepository extends MongoRepository<Concert, String> {
    List<Concert> findByOrganizerId(String organizerId);
    List<Concert> findByStatus(String status);
}
