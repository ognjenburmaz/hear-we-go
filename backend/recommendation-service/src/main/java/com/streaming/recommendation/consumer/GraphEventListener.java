package com.streaming.recommendation.consumer;


import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.UserActivityEvent;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraphEventListener {

    private final Driver neo4jDriver;

    // ---------------- Content Created ----------------
    @KafkaListener(topics = "content-created-topic", groupId = "recommendation-service")
    public void onContentCreated(ContentCreatedEvent e) {

        if (!"SONG".equalsIgnoreCase(e.getType())) {
            return;
        }

        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run(
                        "MERGE (a:Artist {id: $artistId}) " +
                                "SET a.name = $artistName " +
                                "MERGE (g:Genre {name: $genre}) " +
                                "MERGE (s:Song {id: $songId}) " +
                                "SET s.title = $title " +
                                "MERGE (s)-[:BELONGS_TO]->(g) " +
                                "MERGE (a)-[:CREATED]->(s)",
                        Values.parameters(
                                "artistId", e.getArtistId(),
                                "artistName", e.getArtistName(),
                                "genre", e.getGenre(),
                                "songId", e.getId(),
                                "title", e.getTitle()
                        )
                );
                return null;
            });
        }
    }

    @KafkaListener(topics = "user-activity-graph", groupId = "recommendation-service")
    public void onUserActivity(UserActivityEvent e) {

        switch (e.getEventType()) {
            case "GENRE_SUBSCRIBED" -> handleGenreSubscribed(e);
            case "GENRE_UNSUBSCRIBED" -> handleGenreUnsubscribed(e);
            case "RATED" -> handleRated(e);
            case "RATING_REMOVED" -> handleRatingRemoved(e);
            case "REGISTERED" -> handleUserRegistered(e);
            default -> {
            }
        }
    }

    private void handleUserRegistered(UserActivityEvent e) {
        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run(
                        "MERGE (u:User {id: $userId})",
                        Values.parameters(
                                "userId", e.getUserId()
                        )
                );
                return null;
            });
        }
    }

    private void handleGenreSubscribed(UserActivityEvent e) {
        String genre = (String) e.getPayload().get("genre");

        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run(
                        "MERGE (u:User {id: $userId}) " +
                                "MERGE (g:Genre {name: $genre}) " +
                                "MERGE (u)-[:SUBSCRIBED_TO]->(g)",
                        Values.parameters(
                                "userId", e.getUserId(),
                                "genre", genre
                        )
                );
                return null;
            });
        }
    }

    private void handleGenreUnsubscribed(UserActivityEvent e) {
        String genre = (String) e.getPayload().get("genre");

        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run(
                        "MATCH (u:User {id: $userId})-[r:SUBSCRIBED_TO]->(g:Genre {name: $genre}) " +
                                "DELETE r",
                        Values.parameters(
                                "userId", e.getUserId(),
                                "genre", genre
                        )
                );
                return null;
            });
        }
    }

    private void handleRated(UserActivityEvent e) {
        String songId = (String) e.getPayload().get("songId");
        // Safe number handling
        Object val = e.getPayload().get("value");
        int value = (val instanceof Number n) ? n.intValue() : 0;

        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run(
                        "MERGE (u:User {id: $userId}) " +
                                "MERGE (s:Song {id: $songId}) " +
                                "MERGE (u)-[r:RATED]->(s) " +
                                "SET r.value = $value",
                        Values.parameters(
                                "userId", e.getUserId(),
                                "songId", songId,
                                "value", value
                        )
                );
                return null;
            });
        }
    }

    private void handleRatingRemoved(UserActivityEvent e) {
        String songId = (String) e.getPayload().get("songId");

        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run(
                        "MATCH (u:User {id: $userId})-[r:RATED]->(s:Song {id: $songId}) " +
                                "DELETE r",
                        Values.parameters(
                                "userId", e.getUserId(),
                                "songId", songId
                        )
                );
                return null;
            });
        }
    }
}