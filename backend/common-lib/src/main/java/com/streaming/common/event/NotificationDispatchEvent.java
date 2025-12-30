package com.streaming.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDispatchEvent {
    private String userId;
    private String title;
    private String message;
    private String type;
    private String referenceId;
}