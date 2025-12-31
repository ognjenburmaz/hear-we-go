package com.streaming.content.controller;

import com.streaming.content.dto.*;
import com.streaming.content.model.*;
import com.streaming.content.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ArtistService artistService;
    private final ContentService contentService;

// --- ARTISTS ---

    @PostMapping("/artists")
    public ResponseEntity<ArtistResponse> createArtist(@RequestBody @Valid ArtistRequest request) {
        return ResponseEntity.ok(artistService.createArtist(request));
    }

    @GetMapping("/artists")
    public ResponseEntity<List<ArtistResponse>> getAllArtists() {
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
        return ResponseEntity.ok(artistService.updateArtist(id, request));
    }

    // --- ALBUMS ---

    @PostMapping("/albums")
    public ResponseEntity<AlbumResponse> createAlbum(@RequestBody @Valid AlbumRequest request) {
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
        return ResponseEntity.ok(contentService.updateAlbum(id, request));
    }

//    @GetMapping("/albums/{artistId}") //I can't figure out a good path for this, maybe this is a artist endpoint?
//    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtist(@PathVariable String artistId) {
//        return ResponseEntity.ok(contentService.getAlbumsByArtist(artistId));
//    }

    // --- SONGS ---

    @PostMapping(value = "/songs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> addSong(
            @RequestPart("song") @Valid SongRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(contentService.addSong(request, file));
    }

    @GetMapping("/songs")
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        return ResponseEntity.ok(contentService.getAllSongs());
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable String id) {
        return ResponseEntity.ok(contentService.getSongById(id));
    }

    @PutMapping("/songs/{id}")
    public ResponseEntity<SongResponse> updateSong(
            @PathVariable String id,
            @RequestBody @Valid SongRequest request) {
        return ResponseEntity.ok(contentService.updateSong(id, request));
    }

    @GetMapping("/albums/{albumId}/songs")
    public ResponseEntity<List<SongResponse>> getSongsByAlbum(@PathVariable String albumId) {
        return ResponseEntity.ok(contentService.getSongsInAlbum(albumId));
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable String id) {
        contentService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}