package com.example.ticketbooking.dto;

import java.util.List;

public record HotelDto(
        Long hotelId,
        String name,
        String description,
        String country,
        String city,
        String address,
        List<RatePlanDto> ratePlanIds,
        List<Long> highlightIds
) {
}
