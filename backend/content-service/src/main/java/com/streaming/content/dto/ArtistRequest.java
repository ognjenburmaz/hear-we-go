package com.streaming.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArtistRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    // Whitelist: Alphanumeric, spaces, dashes, dots, apostrophes
    @Pattern(regexp = "^[a-zA-Z0-9 \\-'.]+$", message = "Name contains illegal characters")
    private String name;

    @Size(max = 1000, message = "Biography cannot exceed 1000 characters")
    // Broader Whitelist for Bio: Allows punctuation (!?,.) and newlines, but BLOCKS < > (HTML)
    @Pattern(regexp = "^[a-zA-Z0-9 \\-.,!?()'\"\\n\\r&]+$", message = "Biography contains illegal characters (e.g. < or >)")
    private String biography;

    @NotEmpty(message = "At least one genre is required")
    private List<
            @NotBlank
            @Size(max = 20)
            @Pattern(regexp = "^[a-zA-Z0-9 \\-&']+$", message = "Invalid genre format")
                    String
            > genres;
}