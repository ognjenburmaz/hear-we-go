package com.streaming.analytics.service.impl;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import com.streaming.analytics.dto.UserActivityResponse;
import com.streaming.analytics.dto.UserAnalyticsResponse;
import com.streaming.analytics.model.UserActivity;
import com.streaming.analytics.model.UserAnalytics;
import com.streaming.analytics.repository.UserActivityRepository;
import com.streaming.analytics.repository.UserAnalyticsRepository;
import com.streaming.analytics.service.IUserActivityService;
import com.streaming.common.event.UserActivityEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserActivityService implements IUserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final UserAnalyticsRepository analyticsRepository;

    public UserActivityService(UserActivityRepository userActivityRepository, UserAnalyticsRepository analyticsRepository) {
        this.userActivityRepository = userActivityRepository;
        this.analyticsRepository = analyticsRepository;
    }

    public void recordActivity(UserActivityEvent event) {
        UserActivity activity = new UserActivity();
        activity.setUserId(event.getUserId());
        activity.setEventType(event.getEventType());
        activity.setPayload(event.getPayload());
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
        updateAnalytics(event);
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

    @Override
    public UserAnalyticsResponse getUserAnalytics(String userId) {
        UserAnalytics stats = analyticsRepository.findById(userId)
                .orElse(new UserAnalytics());

        return UserAnalyticsResponse.builder()
                .totalSongsListened(stats.getTotalSongsListened())
                .subscribedArtistsCount(stats.getSubscribedArtistsCount())
                .averageRating(stats.getRatingsCount() > 0
                        ? stats.getRatingsSum() / stats.getRatingsCount()
                        : 0.0)
                .songsByGenre(stats.getSongsByGenre())
                .top5Artists(stats.getArtistListenCounts().entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new)))
                .build();
    }

    private void updateAnalytics(UserActivityEvent event) {
        UserAnalytics stats = analyticsRepository.findById(event.getUserId())
                .orElseGet(() -> {
                    UserAnalytics newStats = new UserAnalytics();
                    newStats.setUserId(event.getUserId());
                    return newStats;
                });

        String type = event.getEventType();
        Map<String, Object> payload = event.getPayload();

        switch (type) {
            case "SONG_LISTENED":
                String sId = (String) payload.get("songId");

                if (stats.getListenedSongIds() == null) {
                    stats.setListenedSongIds(new HashSet<>());
                }

                if (!stats.getListenedSongIds().contains(sId)) {
                    stats.setTotalSongsListened(stats.getTotalSongsListened() + 1);
                    stats.getListenedSongIds().add(sId);

                    String genre = (String) payload.getOrDefault("genre", "Unknown");
                    stats.getSongsByGenre().merge(genre, 1L, Long::sum);

                    Object namesObj = payload.get("artistNames");
                    if (namesObj instanceof List) {
                        List<String> names = (List<String>) namesObj;
                        for (String name : names) {
                            stats.getArtistListenCounts().merge(name, 1L, Long::sum);
                        }
                    }
                }
                break;

            case "RATING_SAVED":
                int newValue = (int) payload.get("value");
                Object oldValObj = payload.get("oldValue");

                if (oldValObj != null) {
                    int oldValue = (int) oldValObj;
                    stats.setRatingsSum(stats.getRatingsSum() - oldValue + newValue);
                } else {
                    stats.setRatingsSum(stats.getRatingsSum() + newValue);
                    stats.setRatingsCount(stats.getRatingsCount() + 1);
                }
                break;

            case "RATING_REMOVED":
                if (payload.containsKey("value")) {
                    int removedValue = (int) payload.get("value");
                    stats.setRatingsSum(Math.max(0, stats.getRatingsSum() - removedValue));
                    stats.setRatingsCount(Math.max(0, stats.getRatingsCount() - 1));
                }
                break;

            case "SUB_CREATED":
                stats.setSubscribedArtistsCount(stats.getSubscribedArtistsCount() + 1);
                break;

            case "SUB_DELETED":
                stats.setSubscribedArtistsCount(Math.max(0, stats.getSubscribedArtistsCount() - 1));
                break;
        }

        analyticsRepository.save(stats);
    }
}
