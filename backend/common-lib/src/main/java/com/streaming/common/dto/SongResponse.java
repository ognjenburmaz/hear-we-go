package com.streaming.common.dto;

import lombok.Data;
import java.util.List;

@Data
public class SongResponse {
    private String id;
    private String title;
    private Integer durationSeconds;
    private String genre;
    private String albumId;
    private List<String> artistIds;
    // We do NOT return the internal HDFS path here for security!
    private double averageRating;
    private long totalRatings;
    private Integer userRating;
}