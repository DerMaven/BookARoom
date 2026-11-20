package com.example.ticketbooking.controller;

import com.example.ticketbooking.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBookingAlreadyExists(BookingAlreadyExistsException e) {
        return new ErrorResponse("Бронь уже существует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingCancellation(BookingCancellationException e) {
        return new ErrorResponse("Ошибка отмены бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingCreation(BookingCreationException e) {
        return new ErrorResponse("Ошибка создания бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBookingNotBelongsToGuest(BookingNotBelongsToGuestException e) {
        return new ErrorResponse("Бронирование не принадлежит гостю", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFound(BookingNotFoundException e) {
        return new ErrorResponse("Бронирование не найдено", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBookingNotLocked(BookingNotLockedException e) {
        return new ErrorResponse("Бронирование не заблокировано", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBookingStatus(BookingStatusException e) {
        return new ErrorResponse("Некорректный статус бронирования", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleGuestAlreadyExists(GuestAlreadyExistsException e) {
        return new ErrorResponse("Гость уже существует", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGuestCreation(GuestCreationException e) {
        return new ErrorResponse("Ошибка создания профиля гостя", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleGuestNotFound(GuestNotFoundException e) {
        return new ErrorResponse("Гость не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleHighlightNotFound(HighlightNotFoundException e) {
        return new ErrorResponse("Особенность отеля не найдена", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleHotelNotFound(HotelNotFoundException e) {
        return new ErrorResponse("Отель не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidHotelId(InvalidHotelIdException e) {
        return new ErrorResponse("Некорректный ID отеля", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRevenue(InvalidRevenueException e) {
        return new ErrorResponse("Некорректные данные по выручке", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInventoryNotFound(InventoryNotFoundException e) {
        return new ErrorResponse("Инвентарь не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNightsLimitReached(NightsLimitReachedException e) {
        return new ErrorResponse("Превышен лимит ночей", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePaymentNotFound(PaymentNotFoundException e) {
        return new ErrorResponse("Платеж не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRatePlanNotBelongsToHotel(RatePlanNotBelongsToHotelException e) {
        return new ErrorResponse("Тарифный план не принадлежит отелю", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRatePlanNotFound(RatePlanNotFoundException e) {
        return new ErrorResponse("Тарифный план не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRefundNotFound(RefundNotFoundException e) {
        return new ErrorResponse("Возврат не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRefundType(RefundTypeException e) {
        return new ErrorResponse("Некорректный тип возврата", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleReleasingLock(ReleasingLockException e) {
        return new ErrorResponse("Ошибка снятия блокировки", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRoomMaxOccupancy(RoomMaxOccupancyException e) {
        return new ErrorResponse("Превышена максимальная вместимость номера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRoomNotAvailable(RoomNotAvailableException e) {
        return new ErrorResponse("Номер недоступен", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRoomNotBelongsToHotel(RoomNotBelongsToHotelException e) {
        return new ErrorResponse("Номер не принадлежит отелю", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoomNotFound(RoomNotFoundException e) {
        return new ErrorResponse("Номер не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleStockDecrement(StockDecrementException e) {
        return new ErrorResponse("Ошибка списания остатков", e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleException(Throwable t) {
        return new ErrorResponse("Произошла внезапная ошибка", t.getMessage());
    }

    public record ErrorResponse(String error, String description) {
    }
}
