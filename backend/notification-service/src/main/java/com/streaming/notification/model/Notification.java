package com.streaming.notification.model;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Table("notifications")
public class Notification {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = org.springframework.data.cassandra.core.cql.Ordering.DESCENDING)
    private Instant createdAt;

    @Column("id")
    private UUID id;

    @Column("title")
    private String title;

    @Column("message")
    private String message;

    @Column("is_read")
    private boolean isRead = false;

    // "NEW_SONG", "NEW_ALBUM", "NEW_ARTIST"
    @Column("type")
    private String type;

    // Optional: Link to the content (e.g., songId) for frontend navigation
    @Column("reference_id")
    private String referenceId;
}