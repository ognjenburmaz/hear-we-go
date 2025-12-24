package com.streaming.content.service.impl;

import com.streaming.content.dto.AlbumRequest;
import com.streaming.content.dto.AlbumResponse;
import com.streaming.content.dto.SongRequest;
import com.streaming.content.dto.SongResponse;
import com.streaming.content.model.Album;
import com.streaming.content.model.Song;
import com.streaming.content.repository.AlbumRepository;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ContentMapper mapper;
    private final HdfsStorageService hdfsStorageService;

    @Transactional
    public AlbumResponse createAlbum(AlbumRequest request) {
        Album albumEntity = mapper.toEntity(request);
        Album saved = albumRepository.save(albumEntity);
        return mapper.toResponse(saved);
    }

    public List<AlbumResponse> getAlbumsByArtist(String artistId) {
        return albumRepository.findByArtistIdsContaining(artistId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
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

        // Req 1.11 & 2.6: Emit event for Notifications and Analytics
        // kafkaTemplate.send("content-events", new SongCreatedEvent(saved.getId(), saved.getArtistIds()));

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

        // 2. Emit "SongDeletedEvent" so Ratings/Recommendations can clean up their data
        // kafkaTemplate.send("content-events", new SongDeletedEvent(songId));
    }

    public List<SongResponse> getSongsInAlbum(String albumId) {
        return songRepository.findByAlbumId(albumId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
