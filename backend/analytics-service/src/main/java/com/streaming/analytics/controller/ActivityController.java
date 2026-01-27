package com.streaming.analytics.controller;

import com.streaming.analytics.dto.UserActivityResponse;
import com.streaming.analytics.dto.UserAnalyticsResponse;
import com.streaming.analytics.model.UserActivity;
import com.streaming.analytics.service.IUserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class ActivityController {

    private final IUserActivityService activityService;

    @GetMapping("/history")
    public ResponseEntity<List<UserActivityResponse>> getMyHistory(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(activityService.getUserHistory(userId));
    }

    @GetMapping("/stats")
    public ResponseEntity<UserAnalyticsResponse> getMyStats(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(activityService.getUserAnalytics(userId));
    }
}