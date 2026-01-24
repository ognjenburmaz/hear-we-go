package com.streaming.ratings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;

import java.util.Collections;
import java.util.List;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.cassandra.contact-points:streaming-scylla}")
    private String contactPoints;

    @Value("${spring.cassandra.port:9042}")
    private int port;

    @Value("${SPRING_CASSANDRA_KEYSPACE_NAME_RATINGS}")
    private String keyspaceName;

    @Override
    protected String getKeyspaceName() {
        return keyspaceName;
    }

    @Override
    public String getContactPoints() {
        return contactPoints;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return List.of(
                CreateKeyspaceSpecification.createKeyspace(keyspaceName)
                        .ifNotExists()
                        .withSimpleReplication(1)
        );
    }

    @Override
    protected List<String> getStartupScripts() {
        return List.of(
                "CREATE TABLE IF NOT EXISTS " + keyspaceName + ".ratings (" +
                        "song_id text, " +
                        "user_id text, " +
                        "value int, " +
                        "created_at timestamp, " +
                        "PRIMARY KEY ((song_id), user_id)" +
                        ");",

                "CREATE TABLE IF NOT EXISTS " + keyspaceName + ".rating_stats (" +
                        "song_id text PRIMARY KEY, " +
                        "average_rating double, " +
                        "total_ratings int" +
                        ");"
        );
    }
}