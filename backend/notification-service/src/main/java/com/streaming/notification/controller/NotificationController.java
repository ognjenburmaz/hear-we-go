package com.streaming.notification.controller;

import com.streaming.notification.dto.NotificationResponse;
import com.streaming.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Principal principal) {
        // 1. Probaj iz principala
        String userId = (principal != null) ? principal.getName() : null;

        // 2. Ako je principal null, probaj da vidiš da li Gateway šalje header (često se tako radi)
        if (userId == null) {
            // Dodaj @RequestHeader(value = "X-Auth-User-Id", required = false) u argumente metode ako želiš profi
            userId = "admin"; // Za testiranje dok ne središ Gateway
        }

        System.out.println("Dohvatanje notifikacija za korisnika: {}"+ userId);
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PostMapping("/test")
    public void createTest(@RequestBody com.streaming.common.event.NotificationDispatchEvent event) {
        notificationService.saveNotification(event);
    }

    // Optional: Mark as read endpoint
}