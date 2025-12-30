package com.streaming.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentCreatedEvent {
    private String id;
    private String title;
    private String type;
    private String artistId;
    private String artistName;
    private String genre;
}