package com.streaming.ratings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("rating_stats")
public class RatingStats {

    @PrimaryKey("song_id")
    private String songId;

    @Column("average_rating")
    private double averageRating;

    @Column("total_ratings")
    private int totalRatings;
}