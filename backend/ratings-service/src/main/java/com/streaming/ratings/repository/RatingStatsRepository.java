package com.streaming.ratings.repository;

import com.streaming.ratings.model.RatingStats;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingStatsRepository extends CassandraRepository<RatingStats, String> {
}