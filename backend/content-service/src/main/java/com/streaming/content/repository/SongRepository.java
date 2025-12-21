package com.streaming.content.repository;

import com.streaming.content.model.Song;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SongRepository extends MongoRepository<Song,String> {
    List<Song> findByAlbumId(String albumId);
    List<Song> findByTitleContainingIgnoreCase(String title);
}
