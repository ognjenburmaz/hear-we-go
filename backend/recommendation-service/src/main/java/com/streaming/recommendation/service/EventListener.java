package com.streaming.recommendation.service;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.UserActivityEvent;
import com.streaming.common.event.UserRatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventListener {

    @KafkaListener(topics = {"content-created", "user-activity", "user-rated"}, groupId = "recommendation-group")
    public void handleEvents(Object event) {
        if (event instanceof ContentCreatedEvent cce) {
            System.out.println("ContentCreatedEvent received: " + cce);
            // your logic here
        } else if (event instanceof UserActivityEvent uae) {
            System.out.println("UserActivityEvent received: " + uae);
            // your logic here
        } else if (event instanceof UserRatedEvent ure) {
            System.out.println("UserRatedEvent received: " + ure);
            // your logic here
        } else {
            System.out.println("Unknown event type: " + event.getClass());
        }
    }
}
