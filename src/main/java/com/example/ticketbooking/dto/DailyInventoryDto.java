package com.example.ticketbooking.dto;

import java.time.LocalDateTime;

public record DailyInventoryDto(
        Long inventoryId,
        LocalDateTime inventoryDate,
        Long totalStock,
        Long bookingStock,
        Long availableStock
) {
}
