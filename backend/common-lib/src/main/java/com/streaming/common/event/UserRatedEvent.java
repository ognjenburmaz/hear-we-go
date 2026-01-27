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

//    @JsonCreator
//    public UserRatedEvent(
//            @JsonProperty("userId") String userId,
//            @JsonProperty("songId") String songId,
//            @JsonProperty("value") int value,
//            @JsonProperty("type") String type
//    ) {
//        this.userId = userId;
//        this.songId = songId;
//        this.value = value;
//        this.type = type;
//    }
}
