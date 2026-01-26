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
                OPTIONAL MATCH (u)-[r:RATED]->(s)
                WHERE r IS NULL OR r.value >= 4
                RETURN DISTINCT s
                LIMIT 20
            """)
    List<SongNode> findFromSubscribedGenres(String userId);

    @Query("""
                MATCH (u:User {id: $userId})
                MATCH (s:Song)-[:BELONGS_TO]->(g:Genre)
                WHERE NOT (u)-[:SUBSCRIBED_TO]->(g)
                MATCH (other:User)-[r:RATED {value: 5}]->(s)
                WHERE other.id <> $userId
                RETURN s
                ORDER BY COUNT(r) DESC
                LIMIT 1
            """)
    Optional<SongNode> findTopOutsideGenre(String userId);
}
