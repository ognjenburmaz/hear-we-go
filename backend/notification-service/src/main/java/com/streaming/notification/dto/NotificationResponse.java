package com.streaming.notification.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private Instant createdAt;
    private boolean isRead;
    private String type;
    private String referenceId;
}
