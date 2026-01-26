package com.streaming.ratings.controller;

import com.streaming.common.dto.RatingRequest;
import com.streaming.common.dto.RatingStatsDTO;
import com.streaming.ratings.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Void> rateSong(@RequestBody @Valid RatingRequest request, @RequestHeader("X-User-Id") String userId) {
        ratingService.addRating(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> removeRating(@PathVariable String songId,  @RequestHeader("X-User-Id") String userId) {
        ratingService.deleteSpecific(userId, songId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{songId}/stats")
    public RatingStatsDTO getStats(@PathVariable String songId, @RequestHeader("X-User-Id") String userId) {
        return ratingService.getStatsForSong(songId, userId);
    }

    @PostMapping("/stats/bulk")
    public ResponseEntity<Map<String, RatingStatsDTO>> getBulkStats(
            @RequestBody List<String> songIds,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        Map<String, RatingStatsDTO> statsMap = new HashMap<>();

        for (String id : songIds) {
            statsMap.put(id, ratingService.getStatsForSong(id, userId));
        }

        return ResponseEntity.ok(statsMap);
    }
}