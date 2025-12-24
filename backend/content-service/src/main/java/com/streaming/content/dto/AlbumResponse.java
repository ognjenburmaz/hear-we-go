package com.streaming.content.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AlbumResponse {
    private String id;
    private String title;
    private LocalDate releaseDate;
    private String genre;
    private List<String> artistIds;
}