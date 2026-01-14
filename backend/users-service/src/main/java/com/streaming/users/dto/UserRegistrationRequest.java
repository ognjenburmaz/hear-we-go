package com.streaming.users.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserRegistrationRequest {

    // Whitelisting: Only letters, numbers, and underscores. No < > ' " ;
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 chars")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username contains invalid characters")
    private String username;

    // Boundary Checking & Special Chars
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be 8-64 chars")
    // Complexity check (1 Upper, 1 Lower, 1 Number, 1 Special)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must be strong (Upper, Lower, Number, Special)")
    private String password;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    // Whitelisting: Only letters and spaces
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "First name must contain only letters")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Last name must contain only letters")
    private String lastName;
}