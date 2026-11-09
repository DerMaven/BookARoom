package com.example.ticketbooking.service;

import com.example.ticketbooking.exception.HotelNotFoundException;
import com.example.ticketbooking.exception.RoomNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.Room;
import com.example.ticketbooking.repository.HotelRepository;
import com.example.ticketbooking.repository.RatePlanRepository;
import com.example.ticketbooking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RatePlanRepository ratePlanRepository;

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
}
