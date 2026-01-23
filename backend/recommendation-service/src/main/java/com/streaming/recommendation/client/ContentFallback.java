package com.streaming.recommendation.client;

import com.streaming.common.dto.SongResponse;
import org.springframework.stereotype.Component;

@Component
public class ContentFallback implements ContentClient {

    @Override
    public SongResponse getSongById(String songId) {
        SongResponse fallback = new SongResponse();
        fallback.setId(songId);
        fallback.setTitle("Unknown Title (Service Unavailable)");
        fallback.setGenre("Unknown");
        return fallback;
    }
}