package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    @Query("select rp from RatePlan rp join Hotel h on rp.hotel.id = h.id where h.name = :hotelName")
    List<RatePlan> findByHotelName(String hotelName);

    @Query("select rp from RatePlan rp join Hotel h on rp.hotel.id = h.id where h.id = :hotelId order by rp.price desc limit :size")
    List<RatePlan> findAllByHotelId(Long hotelId, Long size);

    @Query("select rp from RatePlan rp join Hotel h on rp.hotel.id = h.id where h.id = :hotelId and rp.id = :ratePlanId")
    Optional<RatePlan> findByHotelId(Long hotelId, Long ratePlanId);
}
