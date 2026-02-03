package com.streaming.analytics.repository;

import com.streaming.analytics.model.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface UserActivityRepository extends MongoRepository<UserActivity, String> {

    Page<UserActivity> findByUserIdAndEventTypeInOrderByTimestampDesc(
            String userId, List<String> eventTypes, Pageable pageable);
}