package com.streaming.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongDeletedEvent {
    private String songId;
    private String hdfsPath;
}