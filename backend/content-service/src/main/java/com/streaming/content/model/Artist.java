package com.streaming.content.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "artists")
public class Artist {
    @Id
    private String id;

    private String name;
    private String biography;
    private List<String> genres;
    private String imageUrl;
}