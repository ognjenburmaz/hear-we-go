package com.streaming.subscriptions.controller;

import com.streaming.subscriptions.dto.SubscriptionRequest;
import com.streaming.subscriptions.model.UserSubscription;
import com.streaming.subscriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping
    public ResponseEntity<Void> subscribe(@RequestBody SubscriptionRequest request, @RequestHeader("X-User-Id") String userId) {
        service.subscribe(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable String targetId, @RequestHeader("X-User-Id") String userId) {
        service.unsubscribe(userId, targetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserSubscription>> getMySubscriptions(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(service.getUserSubscriptions(userId));
    }
}