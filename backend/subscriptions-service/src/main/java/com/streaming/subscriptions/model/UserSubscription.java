package com.streaming.subscriptions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("subscriptions_by_user")
public class UserSubscription {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userId;

    @PrimaryKeyColumn(name = "target_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String targetId;

    @Column("target_name") // Stored here for CQRS (Req 2.9)
    private String targetName;

    @Column("type")
    private String type;
}