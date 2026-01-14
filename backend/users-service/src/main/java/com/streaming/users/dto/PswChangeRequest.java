package com.streaming.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PswChangeRequest {

    @NotBlank
    private UUID recoveryHash;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be 8-64 chars")
    // Complexity check (1 Upper, 1 Lower, 1 Number, 1 Special)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must be strong (Upper, Lower, Number, Special)")
    private String newPassword;
}
