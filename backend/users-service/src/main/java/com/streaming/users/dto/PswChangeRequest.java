package com.streaming.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PswChangeRequest {
    private UUID recoveryHash;
    private String newPassword;
}
