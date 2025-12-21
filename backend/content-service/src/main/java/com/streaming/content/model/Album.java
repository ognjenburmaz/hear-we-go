package com.streaming.content.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "albums")
public class Album {
    @Id
    private String id;

    private String title;
    private LocalDate releaseDate;
    private String genre;

    private List<String> artistIds;
}
