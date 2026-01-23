package com.streaming.content.repository;

import com.streaming.content.model.Album;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlbumRepository extends MongoRepository<Album,String> {
    List<Album> findByArtistIdsContaining(String artistId);
    List<Album> findTop3ByTitleContainingIgnoreCase(String genre);
}
