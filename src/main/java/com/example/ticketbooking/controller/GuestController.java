package com.example.ticketbooking.controller;

import com.example.ticketbooking.service.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GuestController {
    private final GuestService guestService;

}
