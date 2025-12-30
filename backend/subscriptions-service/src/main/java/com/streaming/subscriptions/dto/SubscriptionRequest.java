package com.streaming.subscriptions.dto;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private String targetId;
    private String targetName; // Required for CQRS
    private String type;
}