package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.BookingDto;
import com.example.ticketbooking.model.enums.PaymentMethod;
import com.example.ticketbooking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/{hotelId}")
    public BookingDto create(@PathVariable Long hotelId,
                             @RequestBody BookingCreateParams params,
                             HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.createBooking(hotelId,
                params.roomId,
                params.ratePlanId,
                params.primaryGuestId,
                params.guestsIds,
                params.paymentMethod,
                params.checkIn,
                params.checkOut
        );
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingDto cancel(@PathVariable Long bookingId,
                             @RequestParam Long guestId,
                             HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.cancelBooking(bookingId, guestId);
    }

    @GetMapping("/all/{guestId}")
    public List<BookingDto> getAllByGuestId(@PathVariable Long guestId,
                                            @RequestBody BookingGetAllParams params,
                                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getAllByGuestId(guestId,
                params.startTime, params.endTime,
                params.size, params.sortBy, params.direction);
    }

    @GetMapping("/all/{hotelId}")
    public List<BookingDto> getAllByHotelId(@PathVariable Long hotelId,
                                            @RequestBody BookingGetAllParams params,
                                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getAllByHotelId(hotelId,
                params.startTime, params.endTime,
                params.size, params.sortBy, params.direction);
    }

    @GetMapping("/{guestId}")
    public BookingDto getByGuestId(@PathVariable Long bookingId,
                                   @RequestParam Long guestId,
                                   HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getByGuestId(bookingId, guestId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId,
                              HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return bookingService.getById(bookingId);
    }

    @Getter
    @Setter
    public class BookingCreateParams{
        private Long roomId;
        private Long ratePlanId;
        private Long primaryGuestId;
        private List<Long> guestsIds;
        private PaymentMethod paymentMethod;
        private LocalDate checkIn;
        private LocalDate checkOut;
    }

    @Getter
    @Setter
    public class BookingGetAllParams{
        private Long id;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer size;
        private String sortBy;
        private String direction;
    }
}
