package com.example.ticketbooking.dto;

import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.model.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BookingDto(
        Long bookingId,
        Long roomId,
        RoomType roomType,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        LocalDateTime checkIn,
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
        LocalDateTime checkOut,
        Long nights,
        Long pricePerNight,
        Long totalAmount,
        Currency currency,
        Boolean isAvailable,
        Long ratePlanId
) {
}
