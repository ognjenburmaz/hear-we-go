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
        // @TODO add this field to the JWT builder
        // 'Principal' comes from the Gateway's JWT (The "sub" field, usually username/email)
        // If you store 'userId' in token, extract it. For now assuming username is the key.
        String userId = principal.getName();

        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PostMapping("/test")
    public void createTest(@RequestBody com.streaming.common.event.NotificationDispatchEvent event) {
        notificationService.saveNotification(event);
    }

    // Optional: Mark as read endpoint
}