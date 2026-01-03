package com.streaming.notification.service.impl;

import com.streaming.common.event.NotificationDispatchEvent;
import com.streaming.notification.dto.NotificationResponse;
import com.streaming.notification.service.NotificationService;
import com.streaming.notification.util.NotificationMapper;
import com.streaming.notification.model.Notification;
import com.streaming.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    public void saveNotification(NotificationDispatchEvent event) {
        log.info("Saving notification for user: {}", event.getUserId());
        Notification notification = mapper.toEntity(event);
        repository.save(notification);
    }

    public List<NotificationResponse> getUserNotifications(String userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}