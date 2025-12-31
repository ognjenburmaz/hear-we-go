package com.streaming.content.service.impl;

import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.content.dto.AlbumRequest;
import com.streaming.content.dto.AlbumResponse;
import com.streaming.content.dto.SongRequest;
import com.streaming.content.dto.SongResponse;
import com.streaming.content.model.Album;
import com.streaming.content.model.Artist;
import com.streaming.content.model.Song;
import com.streaming.content.repository.AlbumRepository;
import com.streaming.content.repository.ArtistRepository;
import com.streaming.content.repository.SongRepository;
import com.streaming.content.service.ContentService;
import com.streaming.content.service.HdfsStorageService;
import com.streaming.content.util.ContentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ContentMapper mapper;
    private final HdfsStorageService hdfsStorageService;

    @Transactional
    public AlbumResponse createAlbum(AlbumRequest request) {
        Album album = mapper.toEntity(request);
        Album savedAlbum = albumRepository.save(album);

        String mainArtistId = savedAlbum.getArtistIds().get(0);
        String artistName = artistRepository.findById(mainArtistId)
                .map(Artist::getName)
                .orElse("Unknown Artist");

        ContentCreatedEvent event = new ContentCreatedEvent(
                savedAlbum.getId(),
                savedAlbum.getTitle(),
                "ALBUM",
                mainArtistId,
                artistName,
                savedAlbum.getGenre()
        );

        kafkaTemplate.send("content-created-topic", event);

        return mapper.toResponse(savedAlbum);
    }

    public List<AlbumResponse> getAlbumsByArtist(String artistId) {
        return albumRepository.findByArtistIdsContaining(artistId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AlbumResponse> getAllAlbums() {
        return albumRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public AlbumResponse getAlbumById(String id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with ID: " + id));
        return mapper.toResponse(album);
    }

    @Transactional
    public AlbumResponse updateAlbum(String id, AlbumRequest request) {
        Album existingAlbum = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with ID: " + id));

        mapper.updateEntity(existingAlbum, request);

        Album saved = albumRepository.save(existingAlbum);
        return mapper.toResponse(saved);
    }

    @Transactional
    public SongResponse addSong(SongRequest request, MultipartFile file) {

        Album album = albumRepository.findById(request.getAlbumId())
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));

        String hdfsPath;
        try {
            hdfsPath = hdfsStorageService.saveFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload audio file", e);
        }

        Song songEntity = mapper.toEntity(request);

        songEntity.setArtistIds(album.getArtistIds());
        songEntity.setAudioFilePath(hdfsPath);

        Song savedSong = songRepository.save(songEntity);

        String mainArtistId = savedSong.getArtistIds().get(0);
        String artistName = artistRepository.findById(mainArtistId)
                .map(Artist::getName)
                .orElse("Unknown Artist");

        ContentCreatedEvent event = new ContentCreatedEvent(
                savedSong.getId(),
                savedSong.getTitle(),
                "SONG",
                mainArtistId,
                artistName,
                savedSong.getGenre()
        );

        kafkaTemplate.send("content-created-topic", event);

        return mapper.toResponse(savedSong);
    }

    @Transactional
    public SongResponse updateSong(String id, SongRequest request) {
        Song existingSong = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with ID: " + id));

        if (!existingSong.getAlbumId().equals(request.getAlbumId())) {
            Album newAlbum = albumRepository.findById(request.getAlbumId())
                    .orElseThrow(() -> new IllegalArgumentException("Target Album not found with ID: " + request.getAlbumId()));

            existingSong.setArtistIds(newAlbum.getArtistIds());
        }

        mapper.updateEntity(existingSong, request);

        Song saved = songRepository.save(existingSong);
        return mapper.toResponse(saved);
    }

    public void deleteSong(String songId) {
        if (!songRepository.existsById(songId)) {
            throw new RuntimeException("Song not found");
        }

        // REQ 1.14 & 2.13 (SAGA PATTERN START)
        // 1. Delete locally
        songRepository.deleteById(songId);
    }

    public List<SongResponse> getAllSongs() {
        return songRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public SongResponse getSongById(String id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with ID: " + id));
        return mapper.toResponse(song);
    }

    public List<SongResponse> getSongsInAlbum(String albumId) {
        return songRepository.findByAlbumId(albumId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
