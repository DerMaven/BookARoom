package com.example.ticketbooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyInventoryDto(
        Long inventoryId,
        LocalDateTime inventoryDate,
        Long totalStock,
        Long bookingStock,
        Long availableStock
) {
}
