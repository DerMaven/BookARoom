package com.example.ticketbooking.dto;

public record RoomDto(
        Long id,
        String roomNumber,
        Long floor,
        String roomType,
        String status
) {
}
