package com.streaming.users.dto;

import lombok.Data;

@Data
public class UserRegistrationResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
}
