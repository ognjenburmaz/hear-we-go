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
    public ResponseEntity<Void> subscribe(@RequestBody SubscriptionRequest request, Principal principal) {
        // @TODO add JWT field
        // Principal.getName() returns the username/id from the JWT token
        service.subscribe(principal.getName(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable String targetId, Principal principal) {
        service.unsubscribe(principal.getName(), targetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserSubscription>> getMySubscriptions(Principal principal) {
        return ResponseEntity.ok(service.getUserSubscriptions(principal.getName()));
    }
}