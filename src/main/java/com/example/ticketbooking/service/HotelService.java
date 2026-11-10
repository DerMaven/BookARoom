package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.HotelDto;
import com.example.ticketbooking.dto.RatePlanDto;
import com.example.ticketbooking.dto.RoomDto;
import com.example.ticketbooking.exception.HotelNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    public HotelDto getById(Long hotelId) {
        Optional<Hotel> hotelCache = redisCacheService.get(String.valueOf(hotelId), Hotel.class);
        if (hotelCache.isPresent()) {
            Hotel hotel = hotelCache.orElseThrow(() -> new HotelNotFoundException("Отель не был найден в кэше"));
            List<RatePlanDto> ratePlanDtos = ratePlanService.getByAllHotelId(hotelId, 10L);
            List<Long> highlightsIds = highlightService.getIdsByAllHotelId(hotelId);
            redisCacheService.set(String.valueOf(hotelId), hotel, Duration.ofMinutes(10));
            return mapHotelToDto(hotel, ratePlanDtos, highlightsIds);
        }
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Отель не был найден в базе данных"));
        List<RatePlanDto> ratePlanDtos = ratePlanService.getByAllHotelId(hotel.getId(), 10L);
        List<Long> highlightsIds = highlightService.getIdsByAllHotelId(hotel.getId());
        redisCacheService.set(String.valueOf(hotel.getId()), hotel, Duration.ofMinutes(10));
        return mapHotelToDto(hotel, ratePlanDtos, highlightsIds);
    }

    public List<HotelDto> getAllHotels(Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? size : 10;
        String sortField = (sortBy != null) ? sortBy : "id";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";

        String cacheKey = "hotels:all:sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<HotelDto> cachedHotels = redisCacheService.getList(cacheKey, HotelDto.class);
        if (!cachedHotels.isEmpty()) {
            return cachedHotels;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Hotel> hotels = hotelRepository.findAllBy(Limit.of(limitSize), sort);

        List<HotelDto> dtos = hotels.stream().map(hotel -> mapHotelToDto(
                        hotel,
                        ratePlanService.getByAllHotelId(hotel.getId(), 10L),
                        highlightService.getIdsByAllHotelId(hotel.getId()))).toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
        }
        return dtos;
    }

    public List<HotelDto> getAllByCity(String city, Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? size : 10;
        String sortField = (sortBy != null) ? sortBy : "id";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";

        String cacheKey = "hotels:city:" + city.toLowerCase() + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<HotelDto> cachedHotels = redisCacheService.getList(cacheKey, HotelDto.class);
        if (!cachedHotels.isEmpty()) {
            return cachedHotels;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Hotel> hotels = hotelRepository.findByCity(city, Limit.of(limitSize), sort);

        List<HotelDto> dtos = hotels.stream().map(hotel -> mapHotelToDto(
                        hotel,
                        ratePlanService.getByAllHotelId(hotel.getId(), 10L),
                        highlightService.getIdsByAllHotelId(hotel.getId())
                )).toList();
        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
        }
        return dtos;
    }

    public List<RoomDto> getAllHotelRooms(Long hotelId, Integer size, String sortBy, String direction) {
        return roomService.getAllHotelRooms(hotelId, size, sortBy, direction);
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

    private HotelDto mapHotelToDto(Hotel hotel, List<RatePlanDto> ratePlans, List<Long> highlightsIds){
        return new HotelDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getAddress(),
                ratePlans,
                highlightsIds
        );
    }
}
