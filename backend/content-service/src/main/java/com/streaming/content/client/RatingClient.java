package com.streaming.content.client;

import com.streaming.common.dto.RatingStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ratings-service")
public interface RatingClient {

    @GetMapping("/api/ratings/{songId}/stats")
    RatingStatsDTO getStats(
            @PathVariable("songId") String songId,
            @RequestHeader("X-User-Id") String userId
    );

    @PostMapping("/api/ratings/stats/bulk")
    Map<String, RatingStatsDTO> getBulkStats(
            @RequestBody List<String> songIds,
            @RequestHeader("X-User-Id") String userId
    );
}
