package com.example.ticketbooking.dto;

import com.example.ticketbooking.model.enums.RoomStatus;
import com.example.ticketbooking.model.enums.RoomType;

public record RoomDto(
        Long id,
        String roomNumber,
        Long floor,
        RoomType roomType,
        RoomStatus status
) {
}
