package com.streaming.notification.service;

import com.streaming.common.event.NotificationDispatchEvent;
import com.streaming.notification.dto.NotificationResponse;

import java.time.Instant;
import java.util.List;

public interface NotificationService {

    void saveNotification(NotificationDispatchEvent event);

    List<NotificationResponse> getUserNotifications(String userId);

    void markAsRead(String userId, Instant createdAt);

    void deleteNotification(String userId, Instant createdAt);
}
