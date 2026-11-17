package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.DateRangeParams;
import com.example.ticketbooking.dto.PaymentDto;
import com.example.ticketbooking.dto.SortParams;
import com.example.ticketbooking.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/hotel/all")
    public List<PaymentDto> getAllByHotelId(@RequestParam Long hotelId,
                                            @RequestParam DateRangeParams rangeParams,
                                            @RequestParam SortParams sortParams,
                                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return paymentService.getAllByHotelId(hotelId, rangeParams, sortParams);
    }

    @GetMapping("/guest/all")
    public List<PaymentDto> getAllByGuestId(@RequestParam Long guestId,
                                            @RequestParam DateRangeParams rangeParams,
                                            @RequestParam SortParams sortParams,
                                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return paymentService.getAllByGuestId(guestId, rangeParams, sortParams);
    }

    @GetMapping("/booking")
    public PaymentDto getByBookingId(@RequestParam Long paymentId,
                              HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return paymentService.getById(paymentId);
    }

    @GetMapping("/id")
    public PaymentDto getById(@RequestParam Long paymentId,
                              HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return paymentService.getById(paymentId);
    }
}
