package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.*;
import com.example.ticketbooking.exception.HotelNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final RatePlanService ratePlanService;
    private final RoomService roomService;
    private final HighlightService highlightService;
    private final RedisCacheService redisCacheService;
    private final HotelRepository hotelRepository;

    private static final List<String> ALLOWED_SORT_FIELDS = new ArrayList<>(List.of("id", "country", "city", "name", "description"));

    public HotelDto getById(Long hotelId) {
        String cacheKey = "hotels:" + hotelId;
        Optional<HotelDto> hotelCache = redisCacheService.get(cacheKey, HotelDto.class);
        if (hotelCache.isPresent()) {
            return hotelCache.get();
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Отель не был найден в базе данных"));
        List<RatePlanDto> ratePlanDtos = ratePlanService.getByAllHotelId(hotel.getId(), new SortParams(10, "price", "desc"));
        List<HighlightDto> highlights = highlightService.getByAllHotelId(hotel.getId());
        HotelDto dto = mapHotelToDto(hotel, ratePlanDtos, highlights);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    public List<HotelDto> getAllHotels(SortParams params) {
        int limitSize = (params.getSize() != null && params.getSize() > 0) ? Math.min(100, params.getSize()) : 10;
        String sortField = (params.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(params.getSortBy())) ? params.getSortBy() : "id";
        String sortDir = (params.getDirection() != null) ? params.getDirection().toLowerCase() : "asc";

        String cacheKey = "hotels:all:sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<HotelDto> cachedHotels = redisCacheService.getList(cacheKey, HotelDto.class);
        if (!cachedHotels.isEmpty()) {
            return cachedHotels;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Hotel> hotels = hotelRepository.findAllBy(Limit.of(limitSize), sort);

        List<HotelDto> dtos = hotels.stream().map(hotel -> mapHotelToDto(
                        hotel,
                        ratePlanService.getByAllHotelId(hotel.getId(), new SortParams(10, "city", "asc")),
                        highlightService.getByAllHotelId(hotel.getId()))).toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
        }
        return dtos;
    }

    public List<HotelDto> getAllByCity(String city, SortParams params) {
        int limitSize = (params.getSize() != null && params.getSize() > 0) ? Math.min(100, params.getSize()) : 10;
        String sortField = (params.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(params.getSortBy())) ? params.getSortBy() : "id";
        String sortDir = (params.getDirection() != null) ? params.getDirection().toLowerCase() : "asc";

        String cacheKey = "hotels:city:" + city.toLowerCase() + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<HotelDto> cachedHotels = redisCacheService.getList(cacheKey, HotelDto.class);
        if (!cachedHotels.isEmpty()) {
            return cachedHotels;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Hotel> hotels = hotelRepository.findByCity(city, Limit.of(limitSize), sort);

        List<HotelDto> dtos = hotels.stream().map(hotel -> mapHotelToDto(
                        hotel,
                        ratePlanService.getByAllHotelId(hotel.getId(), new SortParams(10, "price", "desc")),
                        highlightService.getByAllHotelId(hotel.getId())
                )).toList();
        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
        }
        return dtos;
    }

    public List<RoomDto> getAllHotelRooms(Long hotelId, SortParams params) {
        return roomService.getAllHotelRooms(hotelId, params);
    }

    public List<RoomDto> getAllAvailableHotelRooms(Long hotelId, SortParams params) {
        return roomService.getAllHotelRooms(hotelId, params);
    }

    public List<RatePlanDto> getRatePlans(Long hotelId, SortParams params) {
        return ratePlanService.getByAllHotelId(hotelId, params);
    }

    public RatePlanDto getRatePlan(Long hotelId, Long ratePlanId) {
        return ratePlanService.getByHotelId(hotelId, ratePlanId);
    }

    public Currency getCurrency(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Отель не был найден"))
                .getCurrency();
    }

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }

    private HotelDto mapHotelToDto(Hotel hotel, List<RatePlanDto> ratePlans, List<HighlightDto> highlights){
        return new HotelDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getCountry(),
                hotel.getCity(),
                hotel.getAddress(),
                ratePlans,
                highlights
        );
    }
}
