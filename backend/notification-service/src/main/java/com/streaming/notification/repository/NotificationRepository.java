package com.streaming.notification.repository;

import com.streaming.notification.model.Notification;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends CassandraRepository<Notification, String> {

    @AllowFiltering
    List<Notification> findByUserId(String userId);

    Optional<Notification> findByUserIdAndCreatedAt(String userId, Instant createdAt);


}