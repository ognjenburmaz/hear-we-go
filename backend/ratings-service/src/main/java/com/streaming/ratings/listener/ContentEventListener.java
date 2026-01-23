package com.streaming.ratings.listener;

import com.streaming.common.event.SongDeletedEvent;
import com.streaming.ratings.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentEventListener {

    private final RatingService ratingService;

    @KafkaListener(topics = "content-events-topic", groupId = "ratings-service-saga-group")
    public void handleSongDeleted(SongDeletedEvent event) {
        log.info("Received SongDeletedEvent for ID: {}", event.getSongId());
        ratingService.deleteAllRatingsForSong(event.getSongId());
    }
}