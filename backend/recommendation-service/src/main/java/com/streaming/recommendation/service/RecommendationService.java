package com.streaming.recommendation.service;

import com.streaming.common.dto.SongResponse;
import com.streaming.recommendation.client.ContentClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ContentClient contentClient;

    @CircuitBreaker(name = "content-service")
    @Retry(name = "content-service")
    @TimeLimiter(name = "content-service")
    public CompletableFuture<SongResponse> getRecommendedSongDetails(String songId) {
        // Covers all Resilience features
        return CompletableFuture.supplyAsync(() -> contentClient.getSongById(songId));
    }
}