package com.streaming.notification.repository;

import com.streaming.notification.model.Notification;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends CassandraRepository<Notification, String> {

    List<Notification> findByUserId(String userId);

}