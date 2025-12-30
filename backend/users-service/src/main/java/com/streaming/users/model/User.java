package com.streaming.users.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private String oneTimePasswordHash;
    private UUID recoveryHash;
    private LocalDateTime otpExpiresAt;
    private String role;
    private boolean active;
    private LocalDateTime lastPasswordReset;
}