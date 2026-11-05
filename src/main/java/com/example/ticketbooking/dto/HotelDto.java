package com.example.ticketbooking.dto;

import java.util.List;

public record HotelDto(
        String name,
        List<Long> highlightIds,
        String description,
        String address,
        List<Long> ratePlanIds,
        List<Long> locationIds
) {
}
