package com.streaming.content.dto;

import lombok.Data;
import java.util.List;

@Data
public class ArtistResponse {
    private String id;
    private String name;
    private String biography;
    private List<String> genres;
}