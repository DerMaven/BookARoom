package com.example.ticketbooking.dto;

import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.model.enums.PaymentMethod;

import java.time.LocalDateTime;

public record PaymentDto(
        Long paymentId,
        Long amount,
        Currency currency,
        LocalDateTime processedAt,
        PaymentMethod paymentMethod
) {
}
