//package com.streaming.recommendation.listener;
//
//import com.streaming.common.event.SongDeletedEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class RecommendationSagaConsumer {
//    @TODO
//    private final RecommendationEngine engine;
//
//    @KafkaListener(topics = "song-deleted-topic", groupId = "rec-service-saga-group")
//    public void handleSongDeletion(SongDeletedEvent event) {
//        log.info("Saga: Removing song {} from recommendation indices", event.getSongId());
//        engine.removeFromIndex(event.getSongId());
//    }
//}