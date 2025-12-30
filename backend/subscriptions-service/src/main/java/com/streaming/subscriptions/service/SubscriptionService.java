package com.streaming.subscriptions.service;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.subscriptions.dto.SubscriptionRequest;
import com.streaming.subscriptions.model.UserSubscription;

import java.util.List;

public interface SubscriptionService {

    void subscribe(String userId, SubscriptionRequest request);

    void unsubscribe(String userId, String targetId);

    List<UserSubscription> getUserSubscriptions(String userId);

    void processContentEvent(ContentCreatedEvent event);
}
