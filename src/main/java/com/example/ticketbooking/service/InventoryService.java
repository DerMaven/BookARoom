package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.DailyInventoryDto;
import com.example.ticketbooking.model.DailyInventory;
import com.example.ticketbooking.repository.DailyInventoryRepository;
import com.example.ticketbooking.exception.InventoryNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final DailyInventoryRepository inventoryRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public DailyInventoryDto getAvailability(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        String cacheKey = "inventory:" + hotelId + ":" + checkIn + ":" + checkOut;

        DailyInventory inventoryCache = redisCacheService.get(cacheKey, DailyInventory.class).get();
        if (inventoryCache != null) {
            return mapInventoryToDto(inventoryCache);
        }

        DailyInventory inventory = inventoryRepository.findByHotelId(hotelId, checkIn, checkOut)
                .orElseThrow(() -> new InventoryNotFoundException("Инвентарь не был найден"));

        return mapInventoryToDto(inventory);
    }

    public DailyInventory getInventoryByHotelId(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        return inventoryRepository.findByHotelId(hotelId, checkIn, checkOut)
                .orElseThrow(() -> new InventoryNotFoundException("Инвентарь не был найден"));
    }

    private DailyInventoryDto mapInventoryToDto(DailyInventory inventory) {
        return new DailyInventoryDto(
                inventory.getId(),
                inventory.getInventoryDate(),
                inventory.getTotalStock(),
                inventory.getBookingStock(),
                inventory.getTotalStock() - inventory.getBookingStock()
        );
    }
}
