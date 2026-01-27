package com.streaming.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor(force = true) // Required for Jackson to handle final fields
//@AllArgsConstructor
public class UserActivityEvent {

    private final String userId;
    private final String eventType;
    private final Map<String, Object> payload;

    @JsonCreator
    public UserActivityEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("payload") Map<String, Object> payload
    ) {
        this.userId = userId;
        this.eventType = eventType;
        this.payload = payload;
    }
}