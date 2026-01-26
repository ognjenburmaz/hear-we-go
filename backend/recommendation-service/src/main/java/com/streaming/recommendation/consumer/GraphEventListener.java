package com.streaming.recommendation.consumer;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.UserActivityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GraphEventListener {

    private final Neo4jClient neo4jClient;

    // Content Service → Graph
    @KafkaListener(topics = "content-created", groupId = "recommendation-service")
    public void onContentCreated(ContentCreatedEvent e) {

        if (!"SONG".equalsIgnoreCase(e.getType())) {
            return; // ignore podcasts, etc.
        }

        neo4jClient.query("""
                            MERGE (s:Song {id: $songId})
                            SET s.title = $title
                            MERGE (g:Genre {name: $genre})
                            MERGE (s)-[:BELONGS_TO]->(g)
                            MERGE (a:Artist {id: $artistId})
                            SET a.name = $artistName
                            MERGE (a)-[:CREATED]->(s)
                        """)
                .bindAll(Map.of(
                        "songId", e.getId(),
                        "title", e.getTitle(),
                        "genre", e.getGenre(),
                        "artistId", e.getArtistId(),
                        "artistName", e.getArtistName()
                ))
                .run();
    }

    // User Service / Rating Service → Graph
    @KafkaListener(topics = "user-activity", groupId = "recommendation-service")
    public void onUserActivity(UserActivityEvent e) {

        switch (e.getEventType()) {

            case "GENRE_SUBSCRIBED" -> handleGenreSubscribed(e);

            case "GENRE_UNSUBSCRIBED" -> handleGenreUnsubscribed(e);

            case "RATED" -> handleRated(e);

            case "RATING_REMOVED" -> handleRatingRemoved(e);

            default -> {
                // ignore irrelevant activities
            }
        }
    }

    private void handleGenreSubscribed(UserActivityEvent e) {
        String genre = (String) e.getPayload().get("genre");

        neo4jClient.query("""
                            MERGE (u:User {id: $userId})
                            MERGE (g:Genre {name: $genre})
                            MERGE (u)-[:SUBSCRIBED_TO]->(g)
                        """)
                .bindAll(Map.of(
                        "userId", e.getUserId(),
                        "genre", genre
                ))
                .run();
    }

    private void handleGenreUnsubscribed(UserActivityEvent e) {
        String genre = (String) e.getPayload().get("genre");

        neo4jClient.query("""
                            MATCH (u:User {id: $userId})-[r:SUBSCRIBED_TO]->(g:Genre {name: $genre})
                            DELETE r
                        """)
                .bindAll(Map.of(
                        "userId", e.getUserId(),
                        "genre", genre
                ))
                .run();
    }

    private void handleRated(UserActivityEvent e) {
        String songId = (String) e.getPayload().get("songId");
        int value = (int) e.getPayload().get("value");

        neo4jClient.query("""
                            MERGE (u:User {id: $userId})
                            MERGE (s:Song {id: $songId})
                            MERGE (u)-[r:RATED]->(s)
                            SET r.value = $value
                        """)
                .bindAll(Map.of(
                        "userId", e.getUserId(),
                        "songId", songId,
                        "value", value
                ))
                .run();
    }

    private void handleRatingRemoved(UserActivityEvent e) {
        String songId = (String) e.getPayload().get("songId");

        neo4jClient.query("""
                            MATCH (u:User {id: $userId})-[r:RATED]->(s:Song {id: $songId})
                            DELETE r
                        """)
                .bindAll(Map.of(
                        "userId", e.getUserId(),
                        "songId", songId
                ))
                .run();
    }
}

