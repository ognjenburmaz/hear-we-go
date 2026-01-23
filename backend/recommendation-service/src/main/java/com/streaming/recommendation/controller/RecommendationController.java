package com.streaming.recommendation.controller;

import com.streaming.common.dto.SongResponse;
import com.streaming.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{songId}")
    public CompletableFuture<SongResponse> getRecommendation(@PathVariable String songId) {
        return recommendationService.getRecommendedSongDetails(songId);
    }
}
