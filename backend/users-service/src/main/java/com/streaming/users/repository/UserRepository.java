package com.streaming.users.repository;

import com.streaming.users.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findFirstByEmail(String email);

    Optional<User> findFirstByRecoveryHash(UUID recoveryHash);
}