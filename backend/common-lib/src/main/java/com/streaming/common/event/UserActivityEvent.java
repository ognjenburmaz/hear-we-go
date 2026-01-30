package com.streaming.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor // Required for Jackson to handle final fields
@AllArgsConstructor
public class UserActivityEvent {

    private String userId;
    private String eventType;
    private Map<String, Object> payload;

//    @JsonCreator
//    public UserActivityEvent(
//            @JsonProperty("userId") String userId,
//            @JsonProperty("eventType") String eventType,
//            @JsonProperty("payload") Map<String, Object> payload
//    ) {
//        this.userId = userId;
//        this.eventType = eventType;
//        this.payload = payload;
//    }
}