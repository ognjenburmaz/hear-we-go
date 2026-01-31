package com.streaming.recommendation.consumer;

import com.streaming.common.event.SongDeletedEvent;
import com.streaming.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationSagaConsumer {

    private final RecommendationService recommendationService;

    @KafkaListener(topics = "song-deleted-topic", groupId = "rec-service-saga-group")
    public void handleSongDeletion(SongDeletedEvent event) {
        log.info("Saga: Removing song {} from recommendation indices", event.getSongId());
        recommendationService.removeSongFromGraph(event.getSongId());
    }
}