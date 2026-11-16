package com.example.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DateRangeParams {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
