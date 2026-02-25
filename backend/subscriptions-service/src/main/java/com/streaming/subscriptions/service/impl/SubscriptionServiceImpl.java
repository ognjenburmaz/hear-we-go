package com.streaming.subscriptions.service.impl;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.NotificationDispatchEvent;
import com.streaming.common.event.UserActivityEvent;
import com.streaming.subscriptions.dto.SubscriptionRequest;
import com.streaming.subscriptions.model.TargetSubscriber;
import com.streaming.subscriptions.model.UserSubscription;
import com.streaming.subscriptions.repository.TargetSubscriberRepository;
import com.streaming.subscriptions.repository.UserSubscriptionRepository;
import com.streaming.subscriptions.service.ContentValidationService;
import com.streaming.subscriptions.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository userRepo;
    private final TargetSubscriberRepository targetRepo;
    private final ContentValidationService validationService;
    private final KafkaTemplate<String, NotificationDispatchEvent> kafkaTemplate;
    private final KafkaTemplate<String, Object> genericKafkaTemplate;

    @Override
    public void subscribe(String userId, SubscriptionRequest request) {
        if (!"ARTIST".equalsIgnoreCase(request.getType()) && !"GENRE".equalsIgnoreCase(request.getType()))
            throw new RuntimeException("Type must be either ARTIST or GENRE");

        else if ("ARTIST".equalsIgnoreCase(request.getType())) {
            boolean exists = validationService.doesArtistExist(request.getTargetId());
            if (!exists) {
                throw new IllegalArgumentException("Artist with ID " + request.getTargetId() + " does not exist.");
            }
        } else
            request.setTargetId(request.getTargetId().toUpperCase());

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

        Map<String, Object> payload = Map.of(
                "targetId", request.getTargetId(),
                "targetName", request.getTargetName(),
                "type", request.getType()
        );
        genericKafkaTemplate.send("user-activities", new UserActivityEvent(userId, "SUB_CREATED", payload));

        // Event za graf bazu preporuka
        if ("GENRE".equalsIgnoreCase(request.getType())) {
            UserActivityEvent event = new UserActivityEvent();
            event.setUserId(userId);
            event.setEventType("GENRE_SUBSCRIBED");
            event.setPayload(Map.of("genre", request.getTargetName()));

            genericKafkaTemplate.send("user-activity-graph", event);
        }
    }

    @Override
    public void unsubscribe(String userId, String targetId) {
        UserSubscription subscription = userRepo.findByUserId(userId).stream()
                .filter(sub -> sub.getTargetId().equals(targetId))
                .findFirst()
                .orElse(null);

        String targetName = (subscription != null) ? subscription.getTargetName() : "Unknown Artist";

        userRepo.deleteByUserIdAndTargetId(userId, targetId);
        targetRepo.deleteByTargetIdAndUserId(targetId, userId);

        log.info("User {} unsubscribed from {} ({})", userId, targetName, targetId);

        Map<String, Object> payload = Map.of(
                "targetId", targetId,
                "targetName", targetName
        );

        genericKafkaTemplate.send("user-activities", new UserActivityEvent(userId, "SUB_DELETED", payload));

        // Event za graf bazu preporuka
        if (subscription != null && subscription.getType().equals("GENRE")) {
            UserActivityEvent event = new UserActivityEvent();
            event.setUserId(userId);
            event.setEventType("GENRE_UNSUBSCRIBED");
            event.setPayload(Map.of("genre", subscription.getTargetName()));

            genericKafkaTemplate.send("user-activity-graph", event);
        }
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