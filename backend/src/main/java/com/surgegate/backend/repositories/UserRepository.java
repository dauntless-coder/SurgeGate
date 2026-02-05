package com.surgegate.backend.repositories;

import com.surgegate.backend.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // This allows us to find a user by their email for Login
    Optional<User> findByEmail(String email);
}