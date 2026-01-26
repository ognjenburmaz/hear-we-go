package com.streaming.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityEvent {
    private String userId;
    private String eventType;
    private Map<String, Object> payload;
}