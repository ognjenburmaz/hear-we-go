package com.streaming.ratings.controller;

import com.streaming.common.dto.RatingRequest;
import com.streaming.ratings.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Void> rateSong(@RequestBody @Valid RatingRequest request, Principal principal) {
        ratingService.addRating(principal.getName(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> removeRating(@PathVariable String songId, Principal principal) {
        ratingService.deleteSpecific(principal.getName(), songId);
        return ResponseEntity.noContent().build();
    }
}