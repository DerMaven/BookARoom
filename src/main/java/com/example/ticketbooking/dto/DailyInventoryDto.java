package com.example.ticketbooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public record DailyInventoryDto(
        Long inventoryId,
        LocalDate inventoryDate,
        Long totalStock,
        Long bookingStock,
        Long availableStock
) {
}
