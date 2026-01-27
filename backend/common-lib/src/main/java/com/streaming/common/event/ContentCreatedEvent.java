package com.streaming.common.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true) // <-- Jackson can use this
//@AllArgsConstructor // <-- optional if you want full constructor
public class ContentCreatedEvent {
    private String id;
    private String title;
    private String type;
    private String artistId;
    private String artistName;
    private String genre;

    // Optional immutable style with @JsonCreator
    @JsonCreator
    public ContentCreatedEvent(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("type") String type,
            @JsonProperty("artistId") String artistId,
            @JsonProperty("artistName") String artistName,
            @JsonProperty("genre") String genre
    ) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.artistId = artistId;
        this.artistName = artistName;
        this.genre = genre;
    }
}
