package com.streaming.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class UserRatedEvent {

    private final String userId;
    private final String songId;
    private final int value; // 1-5
    private final String type; // "RATED" or "UNRATED"

    @JsonCreator
    public UserRatedEvent(
            @JsonProperty("userId") String userId,
            @JsonProperty("songId") String songId,
            @JsonProperty("value") int value,
            @JsonProperty("type") String type
    ) {
        this.userId = userId;
        this.songId = songId;
        this.value = value;
        this.type = type;
    }
}
