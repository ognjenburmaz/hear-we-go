package com.streaming.content.grpc;

import com.streaming.common.grpc.*;
import com.streaming.content.model.Artist;
import com.streaming.content.model.Song;
import com.streaming.content.repository.ArtistRepository;
import com.streaming.content.repository.SongRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ContentGrpcService extends ContentServiceGrpcGrpc.ContentServiceGrpcImplBase {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    @Override
    public void checkSongExists(SongIdRequest request, StreamObserver<SongExistsResponse> responseObserver) {
        String songId = request.getSongId();
        boolean exists = songRepository.existsById(songId);

        SongExistsResponse response = SongExistsResponse.newBuilder()
                .setExists(exists)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void checkArtistExists(ArtistIdRequest request, StreamObserver<ArtistExistsResponse> responseObserver) {
        String artistId = request.getArtistId();
        boolean exists = artistRepository.existsById(artistId);

        String name = "";
        if (exists) {
            name = artistRepository.findById(artistId).map(Artist::getName).orElse("");
        }

        ArtistExistsResponse response = ArtistExistsResponse.newBuilder()
                .setExists(exists)
                .setName(name)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getSongDetailsBatch(SongIdsRequest request, StreamObserver<SongListResponse> responseObserver) {
        List<String> ids = request.getSongIdsList();

        List<Song> mongoSongs = songRepository.findAllById(ids);

        List<GrpcSong> grpcSongs = mongoSongs.stream()
                .map(this::mapToGrpcSong)
                .collect(Collectors.toList());

        SongListResponse response = SongListResponse.newBuilder()
                .addAllSongs(grpcSongs)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private GrpcSong mapToGrpcSong(Song song) {
        String artistName = "Unknown Artist";
        if (song.getArtistIds() != null && !song.getArtistIds().isEmpty()) {
            artistName = artistRepository.findById(song.getArtistIds().get(0))
                    .map(Artist::getName)
                    .orElse("Unknown Artist");
        }

        return GrpcSong.newBuilder()
                .setId(song.getId())
                .setTitle(song.getTitle())
                .setGenre(song.getGenre() == null ? "" : song.getGenre())
                .setArtistName(artistName)
                .build();
    }
}