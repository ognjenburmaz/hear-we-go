package com.streaming.analytics.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Document(collection = "user_analytics")
public class UserAnalytics {
    @Id
    private String userId;

    private long totalSongsListened = 0;

    private double ratingsSum = 0;
    private long ratingsCount = 0;

    private Map<String, Long> songsByGenre = new HashMap<>();

    private Map<String, Long> artistListenCounts = new HashMap<>();

    private long subscribedArtistsCount = 0;

    private Set<String> listenedSongIds = new HashSet<>();
}