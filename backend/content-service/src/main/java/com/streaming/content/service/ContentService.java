package com.streaming.content.service;

import com.streaming.content.dto.AlbumRequest;
import com.streaming.content.dto.AlbumResponse;
import com.streaming.content.dto.SongRequest;
import com.streaming.content.dto.SongResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContentService {
    AlbumResponse createAlbum(AlbumRequest album);

    AlbumResponse updateAlbum(String id, AlbumRequest request);

    List<AlbumResponse> getAlbumsByArtist(String artistId);

    SongResponse addSong(SongRequest song, MultipartFile file);

    SongResponse updateSong(String id, SongRequest request);

    void deleteSong(String id);

    List<SongResponse> getSongsInAlbum(String albumId);
}
