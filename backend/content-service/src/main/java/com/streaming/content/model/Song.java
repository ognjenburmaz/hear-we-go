package com.streaming.content.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "songs")
public class Song {
    @Id
    private String id;

    private String title;
    private Integer durationSeconds;
    private String genre;

    private String audioFilePath;

    private String albumId;
    private List<String> artistIds;
}