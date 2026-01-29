package com.streaming.ratings.service.impl;

import com.streaming.common.dto.RatingRequest;
import com.streaming.common.dto.RatingStatsDTO;
import com.streaming.common.event.UserActivityEvent;
import com.streaming.common.event.UserRatedEvent;
import com.streaming.ratings.model.Rating;
import com.streaming.ratings.repository.RatingRepository;
import com.streaming.ratings.repository.RatingStatsRepository;
import com.streaming.ratings.service.RatingService;
import com.streaming.ratings.service.SongValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;
    private final SongValidationService validationService;
    private final KafkaTemplate<String, UserRatedEvent> kafkaTemplate;
    private final RatingStatsRepository statsRepository;
    private final KafkaTemplate<String, Object> genericKafkaTemplate;

    public void addRating(String userId, RatingRequest request) {
        if (!validationService.exists(request.getSongId())) {
            throw new IllegalArgumentException("Song does not exist: " + request.getSongId());
        }

        Optional<Rating> existingRating = repository.findBySongIdAndUserId(request.getSongId(), userId);

        Rating rating = new Rating(
                request.getSongId(),
                userId,
                request.getValue(),
                Instant.now()
        );
        repository.save(rating);

        Map<String, Object> payload = Map.of(
                "songId", request.getSongId(),
                "value", request.getValue(),
                "title", "Song ID: " + request.getSongId()
        );

        UserActivityEvent analyticsEvent = new UserActivityEvent(userId, "RATING_SAVED", payload);
        genericKafkaTemplate.send("user-activities", analyticsEvent);

        String eventType = existingRating.isPresent() ? "UPDATED" : "RATED";
        UserRatedEvent event = new UserRatedEvent(userId, request.getSongId(), request.getValue(), eventType);
        kafkaTemplate.send("rating-events-topic", event);

        if (existingRating.isPresent()) {
            UserActivityEvent userActivityEvent = new UserActivityEvent();
            userActivityEvent.setUserId(userId);
            userActivityEvent.setEventType("RATING_REMOVED");
            userActivityEvent.setPayload(Map.of("songId", request.getSongId(), "value", -1));
            genericKafkaTemplate.send("user-activity-graph", userActivityEvent);
        }

        UserActivityEvent userActivityEvent = new UserActivityEvent();
        userActivityEvent.setUserId(userId);
        userActivityEvent.setEventType("RATED");
        userActivityEvent.setPayload(Map.of("songId", request.getSongId(), "value", request.getValue()));
        genericKafkaTemplate.send("user-activity-graph", userActivityEvent);

        log.info("User {} {} song {} with {}", userId, eventType, request.getSongId(), request.getValue());

    }

    public void removeRating(String userId, String songId) {
        repository.findBySongIdAndUserId(songId, userId).ifPresent(rating -> {
            repository.delete(rating);

            Map<String, Object> payload = Map.of(
                    "songId", songId,
                    "title", "Uklonjena ocena"
            );

            UserActivityEvent analyticsEvent = new UserActivityEvent(userId, "RATING_REMOVED", payload);
            genericKafkaTemplate.send("user-activities", analyticsEvent);

            UserRatedEvent event = new UserRatedEvent(userId, songId, rating.getValue(), "UNRATED");
            kafkaTemplate.send("rating-events-topic", event);

            UserActivityEvent userActivityEvent = new UserActivityEvent();
            userActivityEvent.setUserId(userId);
            userActivityEvent.setEventType("RATING_REMOVED");
            userActivityEvent.setPayload(Map.of("songId", songId, "value", -1));
            genericKafkaTemplate.send("user-activity-graph", userActivityEvent);

            log.info("User {} removed rating for song {}", userId, songId);
        });
    }

    public void deleteSpecific(String userId, String songId) {
        removeRating(userId, songId);
    }

    public void deleteAllRatingsForSong(String songId) {
        repository.deleteBySongId(songId);
        log.info("SAGA: Deleted all ratings for song {}", songId);
    }

    public RatingStatsDTO getStatsForSong(String songId, String userId) {
        RatingStatsDTO dto = statsRepository.findById(songId)
                .map(s -> new RatingStatsDTO(s.getAverageRating(), (long) s.getTotalRatings(), 0)) // dodaj nulu za sad
                .orElse(new RatingStatsDTO(0.0, 0L, 0));

        if (userId != null) {
            repository.findBySongIdAndUserId(songId, userId)
                    .ifPresent(r -> dto.setUserRating(r.getValue()));
        }

        return dto;
    }
}