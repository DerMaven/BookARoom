package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.DailyInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyInventoryRepository extends JpaRepository<DailyInventory, Long> {

    @Query("select di from DailyInventory di where di.hotel.name = :name and di.inventoryDate >= :checkIn and di.inventoryDate < :checkOut order by di.inventoryDate asc")
    Optional<DailyInventory> findByHotelName(String name, LocalDate checkIn, LocalDate checkOut);

    @Query("select di from DailyInventory di where di.hotel.id = :hotelId and di.inventoryDate >= :checkIn and di.inventoryDate < :checkOut order by di.inventoryDate asc")
    Optional<DailyInventory> findByHotelId(Long hotelId, LocalDate checkIn, LocalDate checkOut);
}
