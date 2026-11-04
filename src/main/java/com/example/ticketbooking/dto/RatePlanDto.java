package com.example.ticketbooking.dto;

import java.math.BigDecimal;

public record RatePlanDto(
        Long id,
        String menuPlan,
        BigDecimal price,
        Long beds,
        Long maxNightsStay,
        Long maxOccupancy,
        Boolean isActive,
        Long hotelId,
        Long roomId,
        String roomType
) {
}
