package com.streaming.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityResponse {
    private String eventType;
    private Map<String, Object> payload;
    private String timestamp;
}
