package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.DailyInventory;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyInventoryRepository extends JpaRepository<DailyInventory, Long> {

    @Query("select di from DailyInventory" +
            " di where di.hotel.id = :hotelId" +
            " and di.inventoryDate <= :date" +
            " order by di.inventoryDate desc" +
            " limit 1")
    Optional<DailyInventory> findByHotelId(@Param(value = "hotelId") Long hotelId,
                                           @Param(value = "date") LocalDate date);

    @Query("select di from DailyInventory di" +
            " where di.hotel.id = :hotelId" +
            " and di.inventoryDate >= :startTime" +
            " and di.inventoryDate <= :endTime" +
            " order by di.inventoryDate")
    List<DailyInventory> findAllByHotelId(@Param(value = "hotelId") Long hotelId,
                                          @Param(value = "startTime") LocalDate startTime,
                                          @Param(value = "endTime") LocalDate endTime,
                                          Limit limit);
}
