package com.streaming.content.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ArtistRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String biography;

    private List<String> genres;
}