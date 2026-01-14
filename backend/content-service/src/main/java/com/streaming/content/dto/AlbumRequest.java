package com.streaming.content.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AlbumRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    // Whitelist: Alphanumeric, spaces, dashes, dots, brackets, apostrophes
    @Pattern(regexp = "^[a-zA-Z0-9 \\-_().']+$", message = "Title contains illegal characters")
    private String title;

    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;

    @NotBlank(message = "Genre is required")
    @Size(max = 50, message = "Genre too long")
    // Whitelist: Allow "Rock", "R&B", "Hip-Hop", "Drum'n'Bass"
    @Pattern(regexp = "^[a-zA-Z0-9 \\-&']+$", message = "Genre contains illegal characters")
    private String genre;

    @NotEmpty(message = "Album must have at least one artist")
    // Validate EACH item in the list
    private List<
            @NotBlank
            @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "Invalid Artist ID format")
                    String
            > artistIds;
}