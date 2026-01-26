package com.streaming.analytics.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "user_activities")
public class UserActivity {
    @Id
    private String id;
    private String userId;
    private String eventType;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;


}
