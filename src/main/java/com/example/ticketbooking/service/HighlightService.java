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

    public List<HighlightDto> getByAllHotelId(Long hotelId) {
        String cacheKey = "highlights:" + hotelId;
        List<HighlightDto> highlightCache = redisCacheService.getList(cacheKey, HighlightDto.class);
        if (!highlightCache.isEmpty()) {
            return highlightCache;
        }
        List<Highlight> highlights = highlightRepository.findAllByHotelId(hotelId);
        if (highlights.isEmpty()) {
            return new ArrayList<>();
        }
        List<HighlightDto> dtos = mapHighlightsToDtos(highlights);
        redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
        return dtos;
    }


    private List<HighlightDto> mapHighlightsToDtos(List<Highlight> highlights) {
        return highlights.stream().map(this::mapHighlightToDto).toList();
    }

    private HighlightDto mapHighlightToDto(Highlight highlight) {
        return new HighlightDto(
                highlight.getId(),
                highlight.getDescription(),
                highlight.getIconURL()
        );
    }
}
