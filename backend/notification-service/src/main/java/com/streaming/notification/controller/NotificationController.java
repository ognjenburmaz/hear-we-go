package com.streaming.notification.controller;

import com.streaming.notification.dto.NotificationResponse;
import com.streaming.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@RequestHeader("X-User-Id") String userId) {
        System.out.println("Dohvatanje notifikacija za korisnika: {}"+ userId);
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PostMapping("/test")
    public void createTest(@RequestBody com.streaming.common.event.NotificationDispatchEvent event) {
        notificationService.saveNotification(event);
    }

    @PatchMapping("/mark-as-read")
    public ResponseEntity<Void> markAsRead(@RequestHeader("X-User-Id") String userId, @RequestParam Instant createdAt) {
        notificationService.markAsRead(userId, createdAt);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteNotification(@RequestHeader("X-User-Id") String userId, @RequestParam Instant createdAt) {
        notificationService.deleteNotification(userId, createdAt);

        return ResponseEntity.noContent().build();
    }
}