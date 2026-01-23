package com.streaming.content.controller;

import com.streaming.content.dto.*;
import com.streaming.content.model.Song;
import com.streaming.content.service.ArtistService;
import com.streaming.content.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
        return ResponseEntity.ok(artistService.createArtist(request));
    }

    @GetMapping("/debug/beans")
    public List<String> beans() {
        return Arrays.stream(ctx.getBeanDefinitionNames())
                .filter(b -> b.toLowerCase().contains("file"))
                .toList();
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

    @GetMapping("/artists/{artistId}/albums")
    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtist(@PathVariable String artistId) {
        return ResponseEntity.ok(contentService.getAlbumsByArtist(artistId));
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


    // --- SONGS ---

    @PostMapping(value = "/songs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SongResponse> addSong(
            @RequestPart("song") @Valid SongRequest request,
            @RequestPart("file") MultipartFile file
    ) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
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

    @GetMapping("/songs/{id}/audio")
    public ResponseEntity<StreamingResponseBody> streamAudio(@PathVariable String id) throws IOException {
        Song song = contentService.getSongObjectById(id);
        Path path = new Path(song.getAudioFilePath());

        if (!hdfs.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        StreamingResponseBody body = outputStream -> {
            try (FSDataInputStream in = hdfs.open(path)) {
                IOUtils.copyBytes(in, outputStream, 8192, false);
            }
        };

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(body);
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