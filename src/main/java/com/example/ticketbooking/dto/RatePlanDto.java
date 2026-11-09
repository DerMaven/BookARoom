package com.example.ticketbooking.dto;

import java.math.BigDecimal;

public record RatePlanDto(
        Long ratePlanId,
        String menuPlan,
        BigDecimal price,
        String currency,
        Long beds,
        Long maxNightsStay,
        Boolean isActive
) {
}
