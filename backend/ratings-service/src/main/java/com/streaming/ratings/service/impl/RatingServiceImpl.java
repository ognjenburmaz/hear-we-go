package com.streaming.ratings.service.impl;

import com.streaming.common.dto.RatingRequest;
import com.streaming.common.event.UserRatedEvent;
import com.streaming.ratings.model.Rating;
import com.streaming.ratings.repository.RatingRepository;
import com.streaming.ratings.service.RatingService;
import com.streaming.ratings.service.SongValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;
    private final SongValidationService validationService;
    private final KafkaTemplate<String, UserRatedEvent> kafkaTemplate;

    public void addRating(String userId, RatingRequest request) {
        if (!validationService.exists(request.getSongId())) {
            throw new IllegalArgumentException("Song does not exist: " + request.getSongId());
        }

        Rating rating = new Rating(
                request.getSongId(),
                userId,
                request.getValue(),
                Instant.now()
        );
        repository.save(rating);

        UserRatedEvent event = new UserRatedEvent(userId, request.getSongId(), request.getValue(), "RATED");
        kafkaTemplate.send("rating-events-topic", event);

        log.info("User {} rated song {} with {}", userId, request.getSongId(), request.getValue());
    }

    public void removeRating(String userId, String songId) {
        if (repository.findBySongIdAndUserId(songId, userId).isPresent()) {
            repository.deleteById(songId);

            UserRatedEvent event = new UserRatedEvent(userId, songId, 0, "UNRATED");
            kafkaTemplate.send("rating-events-topic", event);
        }
    }

    public void deleteSpecific(String userId, String songId) {
        repository.findBySongIdAndUserId(songId, userId).ifPresent(repository::delete);
    }

    public void deleteAllRatingsForSong(String songId) {
        repository.deleteBySongId(songId);
        log.info("SAGA: Deleted all ratings for song {}", songId);
    }
}