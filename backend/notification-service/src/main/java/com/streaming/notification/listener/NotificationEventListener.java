package com.streaming.notification.listener;

import com.streaming.common.event.NotificationDispatchEvent;
import com.streaming.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-dispatch-topic", groupId = "notification-service-group")
    public void handleNotificationDispatch(NotificationDispatchEvent event) {
        log.info("Received dispatch event for user: {}", event.getUserId());
        notificationService.saveNotification(event);
    }
}