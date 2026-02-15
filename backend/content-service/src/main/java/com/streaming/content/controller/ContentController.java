package com.streaming.content.controller;

import com.streaming.common.dto.SongResponse;
import com.streaming.content.dto.*;
import com.streaming.content.service.ArtistService;
import com.streaming.content.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ArtistService artistService;
    private final ContentService contentService;
    private final ApplicationContext ctx;
    private final FileSystem hdfs;

// --- ARTISTS ---

    @PostMapping("/artists")
    public ResponseEntity<ArtistResponse> createArtist(@RequestBody @Valid ArtistRequest request) {
        log.info("Artist created!, Name: {}", request.getName());
        return ResponseEntity.ok(artistService.createArtist(request));
    }

    @GetMapping("/artists")
    public ResponseEntity<List<ArtistResponse>> getAllArtists(
            @RequestParam(value = "genre", required = false) List<String> genres) {

        if (genres != null && !genres.isEmpty()) {
            return ResponseEntity.ok(artistService.getArtistsByGenres(genres));
        }

        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable String id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<ArtistResponse> updateArtist(
            @PathVariable String id,
            @RequestBody @Valid ArtistRequest request) {
        log.info("Artist Updated!, New name is: {}", request.getName());
        return ResponseEntity.ok(artistService.updateArtist(id, request));
    }

//    @GetMapping("/artists/{artistId}/albums")
//    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtist(@PathVariable String artistId) {
//        return ResponseEntity.ok(contentService.getAlbumsByArtist(artistId));
//    }

    @GetMapping("/artists/{artistId}/albums")
    public ResponseEntity<ArtistAlbumsResponse> getArtistAlbums(@PathVariable String artistId) {
        return ResponseEntity.ok(contentService.getArtistWithAlbums(artistId));
    }
    // --- ALBUMS ---

    @PostMapping("/albums")
    public ResponseEntity<AlbumResponse> createAlbum(@RequestBody @Valid AlbumRequest request) {
        log.info("Album created!, Name: {}", request.getTitle());
        return ResponseEntity.ok(contentService.createAlbum(request));
    }

    @GetMapping("/albums")
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        return ResponseEntity.ok(contentService.getAllAlbums());
    }

    @GetMapping("/albums/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getAlbumById(id));
    }

    @PutMapping("/albums/{id}")
    public ResponseEntity<AlbumResponse> updateAlbum(
            @PathVariable String id,
            @RequestBody @Valid AlbumRequest request) {
        log.info("Album updated!, Name: {}", request.getTitle());
        return ResponseEntity.ok(contentService.updateAlbum(id, request));
    }


    // --- SONGS ---

    @PostMapping(value = "/songs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> addSong(
            @RequestPart("song") @Valid SongRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        log.info("Song created!, Name: {}", request.getTitle());
        return ResponseEntity.ok(contentService.addSong(request, file));
    }

    @GetMapping("/songs")
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        return ResponseEntity.ok(contentService.getAllSongs());
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<SongResponse> getSongById(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        return ResponseEntity.ok(contentService.getSongById(id, userId));
    }

    @GetMapping("/songs/{id}/audio")
    public ResponseEntity<StreamingResponseBody> streamAudio(@PathVariable String id, @RequestHeader("X-User-Id") String userId) {
        try {
            InputStream audioStream = contentService.getSongAudioStream(id, userId);

            StreamingResponseBody responseBody = outputStream -> {
                try (InputStream is = audioStream) {
                    is.transferTo(outputStream);
                } catch (IOException e) {
                    log.error("Error during HDFS streaming for song {}: {}", id, e.getMessage());
                }
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(responseBody);

        } catch (RuntimeException e) {
            log.warn("Song or file not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("FATAL ERROR streaming song {}: ", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/songs/{id}")
    public ResponseEntity<SongResponse> updateSong(
            @PathVariable String id,
            @RequestBody @Valid SongRequest request) {
        log.info("Song updated!, Name: {}", request.getTitle());
        return ResponseEntity.ok(contentService.updateSong(id, request));
    }

    @GetMapping("/albums/{albumId}/songs")
    public ResponseEntity<List<SongResponse>> getSongsByAlbum(
            @PathVariable String albumId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) { // DODAJ OVO

        return ResponseEntity.ok(contentService.getSongsInAlbum(albumId, userId)); // PROSLEDI ID
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable String id) {
        log.info("Song deleted!, ID: {}", id);
        contentService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> globalSearch(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(new SearchResponse(List.of(), List.of(), List.of()));
        }
        return ResponseEntity.ok(contentService.searchEverything(query));
    }
}