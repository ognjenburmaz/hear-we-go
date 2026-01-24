package com.streaming.ratings.service;

import com.streaming.common.dto.RatingRequest;
import com.streaming.common.dto.RatingStatsDTO;

public interface RatingService {
    void addRating(String userId, RatingRequest request);

    void removeRating(String userId, String songId);

    void deleteSpecific(String userId, String songId);

    void deleteAllRatingsForSong(String songId);

    RatingStatsDTO getStatsForSong(String songId, String userId);
}
