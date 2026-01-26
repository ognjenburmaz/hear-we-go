package com.streaming.analytics.service;

import com.streaming.analytics.dto.UserActivityResponse;
import com.streaming.analytics.model.UserActivity;
import com.streaming.common.event.UserActivityEvent;

import java.util.List;
import java.util.Map;

public interface IUserActivityService{


    void recordActivity(UserActivityEvent event);

    List<UserActivityResponse> getUserHistory(String userId);

    long countActivitiesByUserAndType(String userId, String eventType);

    List<Map<String, Object>> getTopArtists(String userId);
}

