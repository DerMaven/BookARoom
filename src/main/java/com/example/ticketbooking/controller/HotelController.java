package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.HotelDto;
import com.example.ticketbooking.dto.RatePlanDto;
import com.example.ticketbooking.dto.RoomDto;
import com.example.ticketbooking.dto.SortParams;
import com.example.ticketbooking.service.HotelService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelService hotelService;

    @GetMapping("/{hotelId}")
    public HotelDto getById(@PathVariable Long hotelId,
                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getById(hotelId);
    }

    @GetMapping
    public List<HotelDto> getAll(SortParams params,
                                 HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getAllHotels(params);
    }

    @GetMapping("/city")
    public List<HotelDto> getAllByCity(@RequestParam(name = "city") String city,
                                       SortParams params,
                                       HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getAllByCity(city, params);
    }

    @GetMapping("/{hotelId}/rooms")
    public List<RoomDto> getAllAvailableRooms(@PathVariable Long hotelId,
                                              SortParams params,
                                              HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getAllAvailableHotelRooms(
                hotelId,
                params
        );
    }

    @GetMapping("/rooms")
    public List<RoomDto> getAllRooms(@RequestParam Long hotelId,
                                     SortParams params,
                                     HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getAllHotelRooms(
                hotelId,
                params
        );
    }

    @GetMapping("/{hotelId}/rate-plans")
    public List<RatePlanDto> getAllRatePlans(@PathVariable Long hotelId,
                                             SortParams params,
                                             HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getRatePlans(
                hotelId,
                params
        );
    }

    @GetMapping("/{hotelId}/rate-plans/{ratePlanId}")
    public RatePlanDto getRatePlan(@PathVariable Long hotelId,
                                         @PathVariable Long ratePlanId,
                                         HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return hotelService.getRatePlan(
                hotelId,
                ratePlanId
        );
    }
}
