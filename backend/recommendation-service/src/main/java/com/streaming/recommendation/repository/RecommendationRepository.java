package com.streaming.recommendation.repository;

import com.streaming.recommendation.entity.SongNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends Neo4jRepository<SongNode, String> {
    @Query("""
                MATCH (u:User {id: $userId})-[:SUBSCRIBED_TO]->(g:Genre)
                MATCH (s:Song)-[:BELONGS_TO]->(g)
                WHERE NOT EXISTS {
                    MATCH (u)-[r:RATED]->(s)
                    WHERE r.value < 4
                }
                RETURN s
                LIMIT 20
            """)
    List<SongNode> findFromSubscribedGenres(String userId);

    @Query("""
                MATCH (u:User {id: $userId})
                    MATCH (s:Song)-[:BELONGS_TO]->(g:Genre)
                    WHERE NOT (u)-[:SUBSCRIBED_TO]->(g)
                    MATCH (other:User)-[r:RATED {value: 5}]->(s)
                    WHERE other.id <> $userId
                    RETURN s, count(r) AS ratingCount
                    ORDER BY ratingCount DESC
                    LIMIT 1
            """)
    Optional<SongNode> findTopOutsideGenre(String userId);

    @Query("MATCH (s:Song {id: $songId}) DETACH DELETE s")
    void deleteSongNode(String songId);
}
