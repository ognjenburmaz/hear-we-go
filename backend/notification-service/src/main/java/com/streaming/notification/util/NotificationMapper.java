package com.streaming.notification.util;

import com.streaming.common.event.NotificationDispatchEvent;
import com.streaming.notification.dto.NotificationResponse;
import com.streaming.notification.model.Notification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationDispatchEvent event) {
        Notification n = new Notification();
        n.setUserId(event.getUserId());
        n.setCreatedAt(Instant.now());
        n.setId(UUID.randomUUID());
        n.setTitle(event.getTitle());
        n.setMessage(event.getMessage());
        n.setType(event.getType());
        n.setReferenceId(event.getReferenceId());
        n.setRead(false);
        return n;
    }

    public NotificationResponse toResponse(Notification entity) {
        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setMessage(entity.getMessage());
        response.setCreatedAt(entity.getCreatedAt());
        response.setRead(entity.isRead());
        response.setType(entity.getType());
        response.setReferenceId(entity.getReferenceId());
        return response;
    }
}