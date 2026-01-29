package com.streaming.content.service.impl;

import com.streaming.common.dto.RatingStatsDTO;
import com.streaming.common.dto.SongResponse;
import com.streaming.common.event.ContentCreatedEvent;
import com.streaming.common.event.UserActivityEvent;
import com.streaming.content.client.RatingClient;
import com.streaming.content.dto.*;
import com.streaming.content.model.Album;
import com.streaming.content.model.Artist;
import com.streaming.content.model.Song;
import com.streaming.content.repository.AlbumRepository;
import com.streaming.content.repository.ArtistRepository;
import com.streaming.content.repository.SongRepository;
import com.streaming.content.service.ContentService;
import com.streaming.content.service.HdfsStorageService;
import com.streaming.content.util.ContentMapper;
import jakarta.ws.rs.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.Path;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.apache.hadoop.fs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final KafkaTemplate<String, ContentCreatedEvent> kafkaTemplate;
    private final ContentMapper mapper;
    private final HdfsStorageService hdfsStorageService;
    private final FileSystem fileSystem;
    private final RatingClient ratingClient;
    private final KafkaTemplate<String, Object> genericKafkaTemplate;

    private static final List<String> ALLOWED_MIME_TYPES = List.of("audio/mpeg", "audio/wav", "audio/ogg");
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".mp3", ".wav", ".ogg");

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

    public ArtistAlbumsResponse getArtistWithAlbums(String artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        List<AlbumResponse> albums = albumRepository.findByArtistIdsContaining(artistId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return new ArtistAlbumsResponse(artist.getId(), artist.getName(), albums);
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

        validateFile(file);
        String hdfsPath;
        try {
            hdfsPath = hdfsStorageService.saveFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        File tempFile = null;
        int durationInSeconds = -1;
        try {

            tempFile = Files.createTempFile("temp-audio-", file.getOriginalFilename()).toFile();

            file.transferTo(tempFile);

            AudioFile audioFile = AudioFileIO.read(tempFile);
            AudioHeader audioHeader = audioFile.getAudioHeader();

            durationInSeconds = audioHeader.getTrackLength();

        } catch (CannotReadException |
                 TagException |
                 ReadOnlyFileException |
                 IOException |
                 InvalidAudioFrameException e) {

            throw new IllegalArgumentException("Invalid or unreadable audio file", e);
//        } finally {
//            if (tempFile != null && tempFile.exists()) {
//                tempFile.delete();
//            }
        }
        Album album = albumRepository.findById(request.getAlbumId())
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));


        Song songEntity = mapper.toEntity(request);

        songEntity.setDurationSeconds(durationInSeconds);
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

    public SongResponse getSongById(String id, String userId) {
        try {
            if(Objects.equals(id, "67")) {Thread.sleep(4500);}
            Song song = songRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Song not found with ID: " + id));
            SongResponse response = mapper.toResponse(song);

            // API COMPOSITION
            try {
                RatingStatsDTO stats = ratingClient.getStats(id, userId);

                response.setAverageRating(stats.getAverageRating());
                response.setTotalRatings(stats.getTotalRatings());
                response.setUserRating(stats.getUserRating());
            } catch (Exception e) {
                log.error("Greška pri dovlačenju rejtinga: {}", e.getMessage());
                response.setAverageRating(0.0);
                response.setTotalRatings(0L);
                response.setUserRating(0);
            }

            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceUnavailableException();
        }
    }

    public InputStream getSongAudioStream(String songId, String userId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (userId != null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("songId", songId);
            payload.put("title", song.getTitle());
            payload.put("genre", song.getGenre() != null ? song.getGenre() : "Unknown");

            List<String> artistNames = song.getArtistIds().stream()
                    .map(id -> {
                        return artistRepository.findById(id)
                                .map(Artist::getName)
                                .orElse("Nepoznat Izvođač (" + id + ")");
                    })
                    .collect(Collectors.toList());

            payload.put("artistNames", artistNames);

            UserActivityEvent event = new UserActivityEvent(userId, "SONG_LISTENED", payload);
            genericKafkaTemplate.send("user-activities", event);
        }

        String hdfsPathStr = song.getAudioFilePath();
        if (hdfsPathStr == null) {
            throw new RuntimeException("No audio file linked to this song");
        }

        try {
            Path path = new Path(hdfsPathStr);
            if (!fileSystem.exists(path)) {
                throw new FileNotFoundException("File missing in HDFS: " + hdfsPathStr);
            }
            return fileSystem.open(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SongResponse> getSongsInAlbum(String albumId, String userId) {

        List<SongResponse> responses = songRepository.findByAlbumId(albumId)
                .stream()
                .map(mapper::toResponse)
                .toList();

        List<String> songIds = responses.stream()
                .map(SongResponse::getId)
                .toList();

        // API Composition
        try {
            if (!songIds.isEmpty()) {
                Map<String, RatingStatsDTO> statsMap = ratingClient.getBulkStats(songIds, userId);

                responses.forEach(res -> {
                    RatingStatsDTO stats = statsMap.get(res.getId());
                    if (stats != null) {
                        res.setAverageRating(stats.getAverageRating());
                        res.setTotalRatings(stats.getTotalRatings());
                        res.setUserRating(stats.getUserRating());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Failed to fetch ratings for album {}: {}", albumId, e.getMessage());
        }

        return responses;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > 20 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large. Max 20MB");
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains("..")) {
            throw new SecurityException("Cannot store file with relative path outside current directory " + filename);
        }

        String contentType = file.getContentType();
        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type: " + contentType);
        }

        boolean validExtension = ALLOWED_EXTENSIONS.stream()
                .anyMatch(ext -> filename.toLowerCase().endsWith(ext));
        if (!validExtension) {
            throw new IllegalArgumentException("Invalid file extension");
        }
    }

    public SearchResponse searchEverything(String query) {

        List<ArtistResponse> artists = artistRepository.findTop3ByNameContainingIgnoreCase(query)
                .stream()
                .map(artist -> {
                    ArtistResponse res = new  ArtistResponse();
                    res.setId(artist.getId());
                    res.setName(artist.getName());
                    res.setBiography(artist.getBiography());
                    res.setGenres(artist.getGenres());
                    return res;
                })
                .toList();

        List<AlbumResponse> albums = albumRepository.findTop3ByTitleContainingIgnoreCase(query)
                .stream()
                .map(album -> {
                    AlbumResponse res = new  AlbumResponse();
                    res.setId(album.getId());
                    res.setTitle(album.getTitle());
                    res.setReleaseDate(album.getReleaseDate());
                    res.setGenre(album.getGenre());
                    res.setArtistIds(album.getArtistIds());
                    return res;
                })
                .toList();

        List<SongResponse> songs = songRepository.findTop3ByTitleContainingIgnoreCase(query)
                .stream()
                .map(song -> {
                    SongResponse res = new  SongResponse();
                    res.setId(song.getId());
                    res.setTitle(song.getTitle());
                    res.setAlbumId(song.getAlbumId());
                    res.setArtistIds(song.getArtistIds());
                    return res;
                })
                .toList();

        return new SearchResponse(artists, albums, songs);
    }
}
