package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.DailyInventoryDto;
import com.example.ticketbooking.model.DailyInventory;
import com.example.ticketbooking.repository.DailyInventoryRepository;
import com.example.ticketbooking.exception.InventoryNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final DailyInventoryRepository inventoryRepository;
    private final RedisCacheService redisCacheService;

    @Transactional
    public DailyInventory getTodayAvailability(Long hotelId, LocalDate date) {
        String cacheKey = "inventory:" + hotelId + ":" + date;

        Optional<DailyInventory> inventoryCache = redisCacheService.get(cacheKey, DailyInventory.class);
        if (inventoryCache.isPresent()) {
            return inventoryCache.get();
        }

        DailyInventory inventory = inventoryRepository.findByHotelId(hotelId, date)
                .orElseThrow(() -> new InventoryNotFoundException("Инвентарь не был найден"));

        redisCacheService.set(cacheKey, inventory, Duration.ofMinutes(2));
        return inventory;
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
