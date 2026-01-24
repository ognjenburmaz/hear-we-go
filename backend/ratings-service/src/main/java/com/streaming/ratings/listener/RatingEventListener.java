package com.streaming.ratings.listener;

import com.streaming.common.event.UserRatedEvent;
import com.streaming.ratings.model.Rating;
import com.streaming.ratings.model.RatingStats;
import com.streaming.ratings.repository.RatingRepository;
import com.streaming.ratings.repository.RatingStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingEventListener {

    private final RatingStatsRepository statsRepository;
    private final RatingRepository ratingRepository;

    @KafkaListener(topics = "rating-events-topic", groupId = "rating-stats-group")
    public void handleRatingEvent(UserRatedEvent event) {
        log.info("Kafka hvata event tipa: {} za pesmu: {}", event.getType(), event.getSongId());

        List<Rating> allRatings = ratingRepository.findAllBySongId(event.getSongId());

        if (allRatings.isEmpty()) {
            statsRepository.deleteById(event.getSongId());
            log.info("Statistika obrisana jer nema više ocena.");
            return;
        }

        double avg = allRatings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
        int count = allRatings.size();

        RatingStats stats = new RatingStats(event.getSongId(), avg, count);
        statsRepository.save(stats);

        log.info("Statistika ažurirana: Prosek {}, Ukupno glasova {}", avg, count);
    }
}