package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.DailyInventoryDto;
import com.example.ticketbooking.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{hotelId}")
    public DailyInventoryDto getTodayAvailability(@PathVariable Long hotelId,
                                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date,
                                                  HttpServletRequest request) {
        LocalDateTime target = (date != null) ? date : LocalDateTime.now();
        log.info("Выполнен запрос по эндпоинту: '{} {}', Строка параметров запроса: '{}'", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return inventoryService.getTodayAvailabilityDto(hotelId, target);
    }
}
