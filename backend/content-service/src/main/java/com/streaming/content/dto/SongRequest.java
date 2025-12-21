package com.streaming.content.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SongRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @Min(value = 1, message = "Duration must be positive")
    private Integer durationSeconds;

    private String genre;

    @NotBlank(message = "Album ID is mandatory")
    private String albumId;
}
