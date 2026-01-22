package com.streaming.subscriptions.service.impl;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.NotificationDispatchEvent;
import com.streaming.subscriptions.dto.SubscriptionRequest;
import com.streaming.subscriptions.model.TargetSubscriber;
import com.streaming.subscriptions.model.UserSubscription;
import com.streaming.subscriptions.repository.TargetSubscriberRepository;
import com.streaming.subscriptions.repository.UserSubscriptionRepository;
import com.streaming.subscriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository userRepo;
    private final TargetSubscriberRepository targetRepo;
    private final KafkaTemplate<String, NotificationDispatchEvent> kafkaTemplate;

    @Override
    public void subscribe(String userId, SubscriptionRequest request) {
        if ("GENRE".equalsIgnoreCase(request.getType())) {
            request.setTargetId(request.getTargetId().toUpperCase());
        }

        if (userRepo.existsByUserIdAndTargetId(userId, request.getTargetId())) {
            throw new RuntimeException("Already subscribed to " + request.getTargetName());
        }

        UserSubscription userSub = new UserSubscription(
                userId, request.getTargetId(), request.getTargetName(), request.getType().toUpperCase()
        );
        userRepo.save(userSub);

        TargetSubscriber targetSub = new TargetSubscriber(request.getTargetId(), userId);
        targetRepo.save(targetSub);

        log.info("User {} subscribed to {} ({})", userId, request.getTargetName(), request.getType());
    }

    @Override
    public void unsubscribe(String userId, String targetId) {
        userRepo.deleteByUserIdAndTargetId(userId, targetId);
        targetRepo.deleteByTargetIdAndUserId(targetId, userId);
        log.info("User {} unsubscribed from {}", userId, targetId);
    }

    @Override
    public List<UserSubscription> getUserSubscriptions(String userId) {
        return userRepo.findByUserId(userId);
    }

    @Override
    public void processContentEvent(ContentCreatedEvent event) {
        log.info("Processing new content: {} by {}", event.getTitle(), event.getArtistName());

        Set<String> uniqueUserIdsToNotify = new HashSet<>();

        if (event.getArtistId() != null) {
            List<TargetSubscriber> artistSubs = targetRepo.findByTargetId(event.getArtistId());
            artistSubs.forEach(sub -> uniqueUserIdsToNotify.add(sub.getUserId()));
        }

        if (event.getGenre() != null && !event.getGenre().isEmpty()) {
            String normalizedGenre = event.getGenre().toUpperCase();
            List<TargetSubscriber> genreSubs = targetRepo.findByTargetId(normalizedGenre);
            genreSubs.forEach(sub -> uniqueUserIdsToNotify.add(sub.getUserId()));
        }

        uniqueUserIdsToNotify.forEach(userId -> sendNotification(userId, event));

        log.info("Sent notifications to {} users", uniqueUserIdsToNotify.size());
    }

    private void sendNotification(String userId, ContentCreatedEvent event) {
        NotificationDispatchEvent notification = new NotificationDispatchEvent(
                userId,
                "New " + event.getType() + " from " + event.getArtistName(),
                "Check out the new release: " + event.getTitle(),
                "NEW_" + event.getType(),
                event.getId()
        );

        kafkaTemplate.send("notification-dispatch-topic", notification);
    }
}