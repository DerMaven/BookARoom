package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.RoomDto;
import com.example.ticketbooking.exception.HotelNotFoundException;
import com.example.ticketbooking.exception.RoomNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.Room;
import com.example.ticketbooking.repository.HotelRepository;
import com.example.ticketbooking.repository.RatePlanRepository;
import com.example.ticketbooking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RatePlanRepository ratePlanRepository;
    private final RedisCacheService redisCacheService;

    public List<RoomDto> getAllHotelRooms(Long hotelId,
                                          Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? size : 10;
        String sortField = (sortBy != null) ? sortBy : "id";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";

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
                String.valueOf(room.getRoomType()),
                String.valueOf(room.getStatus())
        )).toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofSeconds(55));
        }
        return dtos;
    }

    public boolean belongsToHotel(Long roomId, Long hotelId) {
        Room specificHotelRoom = roomRepository.findByHotel(hotelId)
                .orElseThrow(() -> new RoomNotFoundException("Комната не была найдена"));
        Room chosenRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Комната в этом отеле не была найдена"));;
        return Objects.equals(specificHotelRoom.getHotel().getId(), chosenRoom.getHotel().getId());
    }

    public boolean roomsRatePlanBelongsToHotel(Long roomId, Long ratePlanId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Комната не была найдена"));
        Hotel hotel = hotelRepository.findById(room.getHotel().getId())
                .orElseThrow(() -> new HotelNotFoundException("Отель, который должен принадлежать к комнате с ID: " +
                        roomId + " не был найден"));
        Hotel chosenHotel = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new HotelNotFoundException("Отель, который должен принадлежать к тарифу с ID: " +
                        ratePlanId + " не был найден")).getHotel();
        return Objects.equals(chosenHotel.getId(), hotel.getId());
    }

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }
}
