package com.example.ticketbooking.exception;

public class InvalidHotelIdException extends RuntimeException {
    public InvalidHotelIdException(String message) {
        super(message);
    }
}
