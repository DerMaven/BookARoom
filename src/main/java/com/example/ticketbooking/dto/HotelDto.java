package com.example.ticketbooking.dto;

import java.util.List;

public record HotelDto(
        Long hotelId,
        String name,
        String description,
        String address,
        List<RatePlanDto> ratePlanIds,
        List<Long> highlightIds
) {
}
