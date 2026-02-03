package com.streaming.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArtistAlbumsResponse {
        private String artistId;
        private String artistName;
        private List<AlbumResponse> albums;
}