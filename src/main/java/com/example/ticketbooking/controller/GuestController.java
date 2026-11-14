package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.GuestDto;
import com.example.ticketbooking.dto.UpdateGuestRequest;
import com.example.ticketbooking.service.GuestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/guests")
@RequiredArgsConstructor
@Slf4j
public class GuestController {
    private final GuestService guestService;

    @PostMapping("/new")
    public GuestDto create(@RequestBody GuestParams params,
                           HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return guestService.createGuest(
                params.firstName,
                params.lastName,
                params.email,
                params.phoneNumber,
                params.birthDate,
                params.passportNumber
        );
    }

    @PutMapping("/{guestId}")
    public GuestDto update(@PathVariable Long guestId,
                           @RequestBody UpdateGuestRequest params,
                           HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return guestService.updateGuest(
                guestId,
                params
        );
    }

    @GetMapping("/{guestId}")
    public GuestDto getById(@PathVariable Long guestId,
                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return guestService.getById(guestId);
    }

    @GetMapping
    public GuestDto getByEmail(@RequestParam String email,
                            HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return guestService.getByEmail(email);
    }

    @GetMapping
    public GuestDto getByNumber(@RequestParam String phoneNumber,
                               HttpServletRequest request) {
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return guestService.getByPhoneNumber(phoneNumber);
    }

    @Getter
    @Setter
    public class GuestParams {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private LocalDate birthDate;
        private String passportNumber;
    }

}
