package com.streaming.content.service;

import com.streaming.content.dto.AlbumRequest;
import com.streaming.content.dto.AlbumResponse;
import com.streaming.content.dto.SongRequest;
import com.streaming.content.model.Song;
import com.streaming.common.dto.SongResponse;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import com.streaming.common.dto.SongResponse;
import com.streaming.content.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    SongResponse getSongById(String id);

    InputStream getSongAudioStream(String songId);

    List<SongResponse> getAllSongs();

    List<SongResponse> getSongsInAlbum(String albumId);

    SearchResponse searchEverything(String query);
}
