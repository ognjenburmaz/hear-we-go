package com.streaming.analytics.controller;

import com.streaming.analytics.dto.UserActivityResponse;
import com.streaming.analytics.dto.UserAnalyticsResponse;
import com.streaming.analytics.model.UserActivity;
import com.streaming.analytics.service.IUserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class ActivityController {

    private final IUserActivityService activityService;

    @GetMapping("/history")
    public ResponseEntity<List<UserActivityResponse>> getMyHistory(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam List<String> types,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(activityService.getUserHistory(userId, types, page, size));
    }

    @GetMapping("/stats")
    public ResponseEntity<UserAnalyticsResponse> getMyStats(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(activityService.getUserAnalytics(userId));
    }
}