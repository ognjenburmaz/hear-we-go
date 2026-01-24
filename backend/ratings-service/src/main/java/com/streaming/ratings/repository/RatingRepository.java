package com.streaming.ratings.repository;

import com.streaming.ratings.model.Rating;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends CassandraRepository<Rating, String> {
    Optional<Rating> findBySongIdAndUserId(String songId, String userId);

    void deleteBySongId(String songId);

    List<Rating> findAllBySongId(String songId);
}