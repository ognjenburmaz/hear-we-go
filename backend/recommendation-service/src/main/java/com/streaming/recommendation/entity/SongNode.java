package com.streaming.recommendation.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Song")
@Data
public class SongNode {

    @Id
    private String id;
    private String title;

}
