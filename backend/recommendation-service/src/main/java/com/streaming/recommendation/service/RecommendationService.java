package com.streaming.recommendation.service;

import com.streaming.common.dto.SongResponse;
import com.streaming.recommendation.client.ContentClient;
import com.streaming.recommendation.entity.SongNode;
import com.streaming.recommendation.repository.RecommendationRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
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

//    public List<SongResponse> getHomeRecommendations(String userId) {
//
//        List<SongNode> subscribed = repository.findFromSubscribedGenres(userId);
//
//        Optional<SongNode> outsideTop = repository.findTopOutsideGenre(userId);
//
//        List<SongNode> all = new ArrayList<>(subscribed);
//        outsideTop.ifPresent(all::add);
//
//        return all.stream()
//                .map(song -> contentClient.getSongById(song.getId()))
//                .toList();
//    }

    public List<SongResponse> getHomeRecommendations(String userId) {
        List<SongNode> subscribed = repository.findFromSubscribedGenres(userId);
        Optional<SongNode> outsideTop = repository.findTopOutsideGenre(userId);

        List<SongNode> allNodes = new ArrayList<>(subscribed);
        outsideTop.ifPresent(allNodes::add);

        List<CompletableFuture<SongResponse>> futures = allNodes.stream()
                .map(node -> getRecommendedSongDetails(node.getId()))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .join();
    }

    @Transactional
    public void removeSongFromGraph(String songId) {
        if (repository.existsById(songId)) {
            repository.deleteById(songId);
            log.info("Successfully removed song node {} from Neo4j", songId);
        } else {
            log.warn("Song node {} not found in Neo4j, skipping deletion", songId);
        }
    }

    public CompletableFuture<SongResponse> contentFallback(String songId, Throwable t) {
        SongResponse fallback = new SongResponse();
        fallback.setId(songId);
        fallback.setTitle("Unavailable");
        return CompletableFuture.completedFuture(fallback);
    }

}