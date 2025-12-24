package com.streaming.content.service.impl;

import com.streaming.content.dto.ArtistRequest;
import com.streaming.content.dto.ArtistResponse;
import com.streaming.content.model.Artist;
import com.streaming.content.repository.ArtistRepository;
import com.streaming.content.service.ArtistService;
import com.streaming.content.util.ContentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate; // For Events
    private final ContentMapper mapper;

    public ArtistResponse createArtist(ArtistRequest request) {
        Artist artistEntity = mapper.toEntity(request);

        Artist savedArtist = artistRepository.save(artistEntity);
        // Req 1.11 & 2.6: Notify system about new Artist (for Subscriptions/Notifications)
        // kafkaTemplate.send("content-events", new ArtistCreatedEvent(saved.getId(), saved.getGenres()));
        return mapper.toResponse(savedArtist);
    }

    @Transactional
    public ArtistResponse updateArtist(String id, ArtistRequest request) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found with ID: " + id));

        mapper.updateEntity(existingArtist, request);

        Artist updatedArtist = artistRepository.save(existingArtist);

        return mapper.toResponse(updatedArtist);
    }

    public List<ArtistResponse> getAllArtists() {
        return artistRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<Artist> searchArtists(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }
}
