package com.streaming.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AlbumRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private LocalDate releaseDate;

    private String genre;

    @NotEmpty(message = "Album must have at least one artist")
    private List<String> artistIds;
}