package com.streaming.subscriptions.repository;

import com.streaming.subscriptions.model.TargetSubscriber;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.List;

public interface TargetSubscriberRepository extends CassandraRepository<TargetSubscriber, String> {

    List<TargetSubscriber> findByTargetId(String targetId);

    void deleteByTargetIdAndUserId(String targetId, String userId);
}