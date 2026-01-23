package com.streaming.content.util;

import com.streaming.common.dto.SongResponse;
import com.streaming.content.dto.*;
import com.streaming.content.model.*;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public Artist toEntity(ArtistRequest request) {
        Artist artist = new Artist();
        artist.setName(request.getName());
        artist.setBiography(request.getBiography());
        artist.setGenres(request.getGenres());
        return artist;
    }

    public Album toEntity(AlbumRequest request) {
        Album album = new Album();
        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setGenre(request.getGenre());
        album.setArtistIds(request.getArtistIds());
        return album;
    }

    public Song toEntity(SongRequest request) {
        Song song = new Song();
        song.setTitle(request.getTitle());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setGenre(request.getGenre());
        song.setAlbumId(request.getAlbumId());
        return song;
    }

    public ArtistResponse toResponse(Artist entity) {
        ArtistResponse response = new ArtistResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setBiography(entity.getBiography());
        response.setGenres(entity.getGenres());
        return response;
    }

    public AlbumResponse toResponse(Album entity) {
        AlbumResponse response = new AlbumResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setReleaseDate(entity.getReleaseDate());
        response.setGenre(entity.getGenre());
        response.setArtistIds(entity.getArtistIds());
        return response;
    }

    public SongResponse toResponse(Song entity) {
        SongResponse response = new SongResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDurationSeconds(entity.getDurationSeconds());
        response.setGenre(entity.getGenre());
        response.setAlbumId(entity.getAlbumId());
        response.setArtistIds(entity.getArtistIds());
        return response;
    }

    // --- UPDATE MAPPING (Merge DTO into Existing Entity) ---

    public void updateEntity(Artist entity, ArtistRequest request) {
        entity.setName(request.getName());
        entity.setBiography(request.getBiography());
        entity.setGenres(request.getGenres());
    }

    public void updateEntity(Album entity, AlbumRequest request) {
        entity.setTitle(request.getTitle());
        entity.setReleaseDate(request.getReleaseDate());
        entity.setGenre(request.getGenre());
        entity.setArtistIds(request.getArtistIds());
    }

    public void updateEntity(Song entity, SongRequest request) {
        entity.setTitle(request.getTitle());
        entity.setDurationSeconds(request.getDurationSeconds());
        entity.setGenre(request.getGenre());
        entity.setAlbumId(request.getAlbumId());
    }
}