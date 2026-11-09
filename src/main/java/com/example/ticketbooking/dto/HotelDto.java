package com.example.ticketbooking.dto;

import java.util.List;

public record HotelDto(
        Long hotelId,
        String name,
        List<Long> highlightIds,
        String description,
        String address,
        List<Long> ratePlanIds
) {
}
