package com.streaming.analytics.listener;

import com.streaming.analytics.service.IUserActivityService;
import com.streaming.common.event.UserActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityEventListener {

    private final IUserActivityService userActivityService;

    @KafkaListener(topics = "user-activities", groupId = "analytics-group")
    public void consume(UserActivityEvent event) {
        userActivityService.recordActivity(event);
    }
}
