package com.streaming.ratings.service;

import com.streaming.common.dto.RatingRequest;

public interface RatingService {
    void addRating(String userId, RatingRequest request);

    void removeRating(String userId, String songId);

    void deleteSpecific(String userId, String songId);

    void deleteAllRatingsForSong(String songId);
}
