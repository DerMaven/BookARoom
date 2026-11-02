package com.example.ticketbooking.exception;

public class GuestAlreadyExistsException extends RuntimeException {
    public GuestAlreadyExistsException(String message) {
        super(message);
    }
}
