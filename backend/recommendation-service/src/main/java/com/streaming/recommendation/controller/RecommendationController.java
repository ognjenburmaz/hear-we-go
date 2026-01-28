package com.streaming.recommendation.controller;

import com.streaming.common.dto.SongResponse;
import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.UserActivityEvent;
import com.streaming.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService service;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/{songId}")
    public CompletableFuture<SongResponse> getRecommendation(@PathVariable String songId) {
        return service.getRecommendedSongDetails(songId);
    }

    @GetMapping("/home/{userId}")
    public List<SongResponse> getHome(@PathVariable String userId) {
        return service.getHomeRecommendations(userId);
    }

    @GetMapping("/test1")
    public String sendTest() {
        ContentCreatedEvent event = new ContentCreatedEvent();
        event.setId("song1");
        event.setTitle("Test Song");
        event.setType("SONG");
        event.setArtistId("artist1");
        event.setArtistName("Test Artist");
        event.setGenre("Rock");

        kafkaTemplate.send("content-created", event);

        return "Sent content created event!";
    }

    @GetMapping("/test2")
    public String sendTest2() {
        UserActivityEvent event = new UserActivityEvent();
        event.setUserId("user1");
        event.setEventType("GENRE_SUBSCRIBED");
        event.setPayload(Map.of("genre", "Rock"));

        kafkaTemplate.send("user-activity", event);

        return "Sent genre subscribed event!";
    }

    @GetMapping("/test3")
    public String sendTest3() {
        UserActivityEvent event = new UserActivityEvent();
        event.setUserId("user1");
        event.setEventType("RATED");
        event.setPayload(Map.of("songId", "song1", "value", 5));

        kafkaTemplate.send("user-activity", event);

        return "Sent user rating event!";
    }
}
