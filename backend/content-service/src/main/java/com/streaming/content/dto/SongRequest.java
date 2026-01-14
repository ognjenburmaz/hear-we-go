package com.streaming.content.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SongRequest {

    @NotBlank
    @Size(max = 100, message = "Title too long")
    // Sanitization: Allow letters, numbers, spaces, and basic punctuation (-_().)
    @Pattern(regexp = "^[a-zA-Z0-9 \\-_().]+$", message = "Title contains illegal characters")
    private String title;

    @Min(value = 1, message = "Duration must be positive")
    @Max(value = 3600, message = "Song cannot be longer than 1 hour")
    private Integer durationSeconds;

    @NotBlank(message = "Genre is mandatory")
    @Pattern(regexp = "^[a-zA-Z0-9 \\-&']+$", message = "Invalid genre format")
    private String genre;

    @NotBlank(message = "Album ID is mandatory")
    private String albumId;
}
