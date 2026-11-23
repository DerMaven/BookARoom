package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.RatePlan;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    List<RatePlan> findAllByHotelId(@Param(value = "hotelId") Long hotelId,
                                    Limit limit,
                                    Sort sort);

    Optional<RatePlan> findByHotelIdAndId(@Param(value = "hotelId") Long hotelId,
                                          @Param(value = "ratePlanId") Long ratePlanId);
}
