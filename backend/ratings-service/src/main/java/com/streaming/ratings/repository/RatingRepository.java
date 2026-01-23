package com.streaming.ratings.repository;

import com.streaming.ratings.model.Rating;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.Optional;

public interface RatingRepository extends CassandraRepository<Rating, String> {
    Optional<Rating> findBySongIdAndUserId(String songId, String userId);

    void deleteBySongId(String songId);
}