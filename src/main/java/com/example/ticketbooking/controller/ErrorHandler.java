package com.example.ticketbooking.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ErrorResponse handleException(Throwable t) {
        return new ErrorResponse("Произошла внезапная ошибка", t.getMessage());
    }

    @Getter
    @RequiredArgsConstructor
    public static class ErrorResponse {
        private final String error;
        private final String description;
    }
}
