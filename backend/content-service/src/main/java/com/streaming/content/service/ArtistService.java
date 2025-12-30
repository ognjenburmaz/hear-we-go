package com.streaming.content.service;

import com.streaming.content.dto.ArtistRequest;
import com.streaming.content.dto.ArtistResponse;
import com.streaming.content.model.Artist;

import java.util.List;

public interface ArtistService {
    ArtistResponse createArtist(ArtistRequest artist);

    List<ArtistResponse> getAllArtists();

    ArtistResponse updateArtist(String id, ArtistRequest request);

    ArtistResponse getArtistById(String id);
}
