package com.example.ticketbooking.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {



    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final String error;
        private final String description;
    }
}
