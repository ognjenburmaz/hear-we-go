package com.streaming.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAnalyticsResponse {
    private long totalSongsListened;
    private double averageRating;
    private Map<String, Long> songsByGenre;
    private Map<String, Long> top5Artists;
    private long subscribedArtistsCount;
}