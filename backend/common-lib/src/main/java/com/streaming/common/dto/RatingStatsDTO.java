package com.streaming.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingStatsDTO {
    private double averageRating;
    private long totalRatings;
    private Integer userRating;
}