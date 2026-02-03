package com.streaming.analytics.repository;

import com.streaming.analytics.model.UserAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAnalyticsRepository extends MongoRepository<UserAnalytics, String> {

}
