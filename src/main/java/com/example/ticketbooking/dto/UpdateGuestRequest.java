package com.example.ticketbooking.dto;

import java.time.LocalDate;
import java.util.Optional;

public record UpdateGuestRequest(
        Optional<String> firstName,
        Optional<String> lastName,
        Optional<LocalDate> birthDate,
        Optional<String> email,
        Optional<String> phoneNumber,
        Optional<String> passportNumber
) {
}
