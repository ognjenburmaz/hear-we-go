package com.streaming.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResponse {
    private List<ArtistResponse> artists;
    private List<AlbumResponse> albums;
    private List<SongResponse> songs;
}
