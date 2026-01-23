package com.streaming.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRatedEvent {
    private String userId;
    private String songId;
    private int value; // 1-5
    private String type; // "RATED" or "UNRATED"
}