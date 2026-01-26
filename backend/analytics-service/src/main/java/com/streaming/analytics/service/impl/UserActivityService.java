package com.streaming.analytics.service.impl;

import com.streaming.analytics.dto.UserActivityResponse;
import com.streaming.analytics.model.UserActivity;
import com.streaming.analytics.repository.UserActivityRepository;
import com.streaming.analytics.service.IUserActivityService;
import com.streaming.common.event.UserActivityEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserActivityService implements IUserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public void recordActivity(UserActivityEvent event) {
        UserActivity activity = new UserActivity();
        activity.setUserId(event.getUserId());
        activity.setEventType(event.getEventType());
        activity.setPayload(event.getPayload());
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    @Override
    public List<UserActivityResponse> getUserHistory(String userId) {
        return userActivityRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(activity -> new UserActivityResponse(
                        activity.getEventType(),
                        activity.getPayload(),
                        activity.getTimestamp().toString()
                ))
                .toList();
    }

    @Override
    public long countActivitiesByUserAndType(String userId, String eventType) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> getTopArtists(String userId) {
        return List.of();
    }

    public void save(UserActivity userActivity){
        userActivityRepository.save(userActivity);
    }
}
