package com.streaming.content.repository;

import com.streaming.content.model.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArtistRepository extends MongoRepository<Artist,String> {
    List<Artist> findByGenresContaining(String genre);
    List<Artist> findByNameContainingIgnoreCase(String name);
}
