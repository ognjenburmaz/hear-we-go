package com.streaming.content.service.impl;

import com.streaming.common.event.ContentCreatedEvent;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final KafkaTemplate<String, ContentCreatedEvent> kafkaTemplate;
    private final ContentMapper mapper;

    public ArtistResponse createArtist(ArtistRequest request) {
        Artist artistEntity = mapper.toEntity(request);

        Artist savedArtist = artistRepository.save(artistEntity);

        ContentCreatedEvent event = new ContentCreatedEvent(
                savedArtist.getId(),
                savedArtist.getName(),
                "ARTIST",
                savedArtist.getId(),
                savedArtist.getName(),
                savedArtist.getGenres().isEmpty() ? "Unknown" : savedArtist.getGenres().get(0)
        );

        kafkaTemplate.send("content-created-topic", event);
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

    public ArtistResponse getArtistById(String id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artist not found with ID: " + id));
        return mapper.toResponse(artist);
    }

    public List<Artist> searchArtists(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name);
    }

    public List<ArtistResponse> getArtistsByGenres(List<String> genres) {
        List<java.util.regex.Pattern> patterns = genres.stream()
                .map(g -> java.util.regex.Pattern.compile("^" + java.util.regex.Pattern.quote(g) + "$",
                        java.util.regex.Pattern.CASE_INSENSITIVE))
                .toList();

        return artistRepository.findByGenresInList(patterns)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
