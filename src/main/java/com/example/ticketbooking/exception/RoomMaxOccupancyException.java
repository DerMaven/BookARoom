package com.example.ticketbooking.exception;

public class RoomMaxOccupancyException extends RuntimeException {
    public RoomMaxOccupancyException(String message) {
        super(message);
    }
}
