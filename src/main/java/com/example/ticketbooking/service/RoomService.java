package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.RoomDto;
import com.example.ticketbooking.dto.SortParams;
import com.example.ticketbooking.exception.HotelNotFoundException;
import com.example.ticketbooking.exception.RatePlanNotFoundException;
import com.example.ticketbooking.exception.RoomNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.RatePlan;
import com.example.ticketbooking.model.Room;
import com.example.ticketbooking.repository.HotelRepository;
import com.example.ticketbooking.repository.RatePlanRepository;
import com.example.ticketbooking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RatePlanRepository ratePlanRepository;
    private final RedisCacheService redisCacheService;
    private static final List<String> ALLOWED_SORT_FIELDS = new ArrayList<>(List.of("id", "roomType", "status", "price"));

    public List<RoomDto> getAllHotelRooms(Long hotelId,
                                          SortParams params) {
        int limitSize = (params.getSize() != null && params.getSize() > 0) ? Math.min(100, params.getSize()) : 10;
        String sortField = (params.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(params.getSortBy())) ? params.getSortBy() : "id";
        String sortDir = (params.getDirection() != null) ? params.getDirection().toLowerCase() : "asc";

        String cacheKey = "rooms:hotel:" + hotelId + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<RoomDto> cachedRooms = redisCacheService.getList(cacheKey, RoomDto.class);
        if (!cachedRooms.isEmpty()) {
            return cachedRooms;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Room> rooms = roomRepository.findAllByHotelId(hotelId, Limit.of(limitSize), sort);

        List<RoomDto> dtos = rooms.stream().map(room -> new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getRoomType(),
                room.getStatus()
        )).toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofSeconds(55));
        }
        return dtos;
    }

    public List<RoomDto> getAllAvailableHotelRooms(Long hotelId,
                                          SortParams params) {
        int limitSize = (params.getSize() != null && params.getSize() > 0) ? Math.min(100, params.getSize()) : 10;
        String sortField = (params.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(params.getSortBy())) ? params.getSortBy() : "id";
        String sortDir = (params.getDirection() != null) ? params.getDirection().toLowerCase() : "asc";

        String cacheKey = "rooms:hotel:" + hotelId + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<RoomDto> cachedRooms = redisCacheService.getList(cacheKey, RoomDto.class);
        if (!cachedRooms.isEmpty()) {
            return cachedRooms;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Room> rooms = roomRepository.findAllByHotelId(hotelId, Limit.of(limitSize), sort)
                .stream()
                .filter(r -> r.getIsAvailable() == true)
                .toList();

        List<RoomDto> dtos = rooms.stream().map(room -> new RoomDto(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getRoomType(),
                room.getStatus()
        )).toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofSeconds(55));
        }
        return dtos;
    }

    public boolean belongsToHotel(Long roomId, Long hotelId) {
        return roomRepository.existsByIdAndHotelId(roomId, hotelId);
    }

    public boolean roomsRatePlanBelongsToHotel(Long hotelId, Long ratePlanId) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new RatePlanNotFoundException("Тариф с ID " + ratePlanId + " не был найден"));
        return Objects.equals(ratePlan.getHotel().getId(), hotelId);
    }

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }
}
