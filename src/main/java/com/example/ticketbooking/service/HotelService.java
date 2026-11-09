package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.RatePlanDto;
import com.example.ticketbooking.exception.HotelNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final RatePlanService ratePlanService;
    private final HotelRepository hotelRepository;

    public Hotel getById(Long hotelID) {
        return hotelRepository.findById(hotelID)
                .orElseThrow(() -> new HotelNotFoundException("Отель не был найден"));
    }

    public List<RatePlanDto> getAllRatePlans(Long hotelId, Long size) {
        return ratePlanService.getByAllHotelId(hotelId, size);
    }

    public RatePlanDto getRatePlan(Long hotelId, Long ratePlanId) {
        return ratePlanService.getByHotelId(hotelId, ratePlanId);
    }

    public Currency getCurrency(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Отель не был найден"))
                .getCurrency();
    }
}
