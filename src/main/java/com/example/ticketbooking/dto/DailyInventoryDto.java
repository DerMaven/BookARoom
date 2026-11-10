package com.example.ticketbooking.dto;

import java.time.LocalDate;

public record DailyInventoryDto(
        Long inventory_id,
        LocalDate inventoryDate,
        Long totalStock,
        Long bookingStock,
        Long availableStock
) {
}
