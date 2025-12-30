package com.streaming.subscriptions.listener;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.subscriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentEventListener {

    private final SubscriptionService subscriptionService;

    @KafkaListener(topics = "content-created-topic", groupId = "subscription-service-group")
    public void handleContentCreated(ContentCreatedEvent event) {
        subscriptionService.processContentEvent(event);
    }
}