package com.example.ticketbooking.dto;

import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.model.enums.RoomType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingDto(
        Long bookingId,
        Long roomId,
        RoomType roomType,
        LocalDateTime checkIn,
        LocalDateTime checkOut,
        Long nights,
        Long pricePerNight,
        Long totalAmount,
        Currency currency,
        Boolean isAvailable,
        Long ratePlanId
) {
}
