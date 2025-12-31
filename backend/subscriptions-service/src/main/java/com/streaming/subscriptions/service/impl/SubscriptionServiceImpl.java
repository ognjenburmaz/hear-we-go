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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository userRepo;
    private final TargetSubscriberRepository targetRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void subscribe(String userId, SubscriptionRequest request) {
        if (userRepo.existsByUserIdAndTargetId(userId, request.getTargetId())) {
            throw new RuntimeException("Already subscribed");
        }

        // Save for User Profile (CQRS: Store name here so we don't query Content Service later)
        UserSubscription userSub = new UserSubscription(
                userId, request.getTargetId(), request.getTargetName(), request.getType()
        );
        userRepo.save(userSub);

        TargetSubscriber targetSub = new TargetSubscriber(request.getTargetId(), userId);
        targetRepo.save(targetSub);

        log.info("User {} subscribed to {}", userId, request.getTargetName());
    }

    public void unsubscribe(String userId, String targetId) {
        userRepo.deleteByUserIdAndTargetId(userId, targetId);
        targetRepo.deleteByTargetIdAndUserId(targetId, userId);
    }

    public List<UserSubscription> getUserSubscriptions(String userId) {
        return userRepo.findByUserId(userId);
    }


    public void processContentEvent(ContentCreatedEvent event) {
        log.info("Processing new content: {} by {}", event.getTitle(), event.getArtistName());

        List<TargetSubscriber> artistSubscribers = targetRepo.findByTargetId(event.getArtistId());

        // Find subscribers for the Genre (if applicable)
        // List<TargetSubscriber> genreSubscribers = targetRepo.findByTargetId(event.getGenre());

        artistSubscribers.forEach(sub -> sendNotification(sub.getUserId(), event));
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