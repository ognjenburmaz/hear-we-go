package com.streaming.content.repository;

import com.streaming.content.model.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistRepository extends MongoRepository<Artist,String> {
    List<Artist> findByGenresContaining(String genre);
    List<Artist> findByNameContainingIgnoreCase(String name);
    List<Artist> findTop3ByNameContainingIgnoreCase(String name);

    @Query("{ 'genres': { $in: ?0 } }")
    List<Artist> findByGenresInList(List<java.util.regex.Pattern> patterns);
}