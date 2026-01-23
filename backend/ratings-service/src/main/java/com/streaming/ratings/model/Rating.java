package com.streaming.ratings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("ratings")
public class Rating {

    @PrimaryKeyColumn(name = "song_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String songId;

    @PrimaryKeyColumn(name = "user_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String userId;

    @Column("value")
    private int value;

    @Column("created_at")
    private Instant createdAt;
}