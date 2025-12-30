package com.streaming.subscriptions.repository;

import com.streaming.subscriptions.model.UserSubscription;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.List;

public interface UserSubscriptionRepository extends CassandraRepository<UserSubscription, String> {

    List<UserSubscription> findByUserId(String userId);

    void deleteByUserIdAndTargetId(String userId, String targetId);

    boolean existsByUserIdAndTargetId(String userId, String targetId);
}
