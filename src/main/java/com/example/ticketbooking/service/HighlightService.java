package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.HighlightDto;
import com.example.ticketbooking.model.Highlight;
import com.example.ticketbooking.repository.HighlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HighlightService {

    private final HighlightRepository highlightRepository;
    private final RedisCacheService redisCacheService;

    public List<Long> getIdsByAllHotelId(Long hotelId) {
        String cacheKey = "highlights:" + hotelId;
        List<Highlight> highlightCache = redisCacheService.getList(cacheKey, Highlight.class);
        if (highlightCache != null) {
            List<HighlightDto> dtos = this.mapHighlightsToDtos(highlightCache);
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
            return mapDtosToIds(dtos);
        }
        List<Highlight> highlights = highlightRepository.findAllByHotelId(hotelId);
        if (highlights == null) {
            return new ArrayList<>();
        }
        redisCacheService.set(cacheKey, mapHighlightsToDtos(highlights), Duration.ofMinutes(10));
        return mapDtosToIds(mapHighlightsToDtos(highlights));
    }


    private List<HighlightDto> mapHighlightsToDtos(List<Highlight> highlights) {
        return highlights.stream().map(this::mapHighlightToDto).toList();
    }

    private List<Long> mapDtosToIds(List<HighlightDto> dtos) {
        return dtos.stream().map(HighlightDto::id).toList();
    }

    private HighlightDto mapHighlightToDto(Highlight highlight) {
        return new HighlightDto(
                highlight.getId(),
                highlight.getDescription(),
                highlight.getIconURL()
        );
    }
}
