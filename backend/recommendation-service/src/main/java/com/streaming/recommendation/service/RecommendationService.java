package com.streaming.recommendation.service;

import com.streaming.common.dto.SongResponse;
import com.streaming.recommendation.client.ContentClient;
import com.streaming.recommendation.entity.SongNode;
import com.streaming.recommendation.repository.RecommendationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;
    private final ContentClient contentClient;

    @CircuitBreaker(name = "content-service")
    @Retry(name = "content-service")
    @TimeLimiter(name = "content-service")
    public CompletableFuture<SongResponse> getRecommendedSongDetails(String songId) {
        // Covers all Resilience features
        return CompletableFuture.supplyAsync(() -> contentClient.getSongById(songId));
    }

    public List<SongResponse> getHomeRecommendations(String userId) {

        List<SongNode> subscribed = repository.findFromSubscribedGenres(userId);

        Optional<SongNode> outsideTop = repository.findTopOutsideGenre(userId);

        List<SongNode> all = new ArrayList<>(subscribed);
        outsideTop.ifPresent(all::add);

        return all.stream()
                .map(song -> contentClient.getSongById(song.getId()))
                .toList();
    }


}