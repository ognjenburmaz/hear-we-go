package com.streaming.recommendation.client;

import com.streaming.common.dto.SongResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "content-service", fallback = ContentFallback.class)
public interface ContentClient {

    @GetMapping("/api/content/songs/{id}")
    SongResponse getSongById(@PathVariable("id") String id);
}