package com.streaming.content.service;

import com.streaming.common.dto.SongResponse;
import com.streaming.content.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ContentService {
    AlbumResponse createAlbum(AlbumRequest album);

    AlbumResponse updateAlbum(String id, AlbumRequest request);

    AlbumResponse getAlbumById(String id);

    List<AlbumResponse> getAllAlbums();

    List<AlbumResponse> getAlbumsByArtist(String artistId);

    SongResponse addSong(SongRequest song, MultipartFile file);

    SongResponse updateSong(String id, SongRequest request);

    void deleteSong(String id);

    SongResponse getSongById(String id, String userId);

    InputStream getSongAudioStream(String songId, String userId);

    List<SongResponse> getAllSongs();

    List<SongResponse> getSongsInAlbum(String albumId, String userId);

    SearchResponse searchEverything(String query);

    ArtistAlbumsResponse getArtistWithAlbums(String id);

}
