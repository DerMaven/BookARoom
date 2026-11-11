package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r join Hotel h on h.id = r.hotel.id where h.name = :hotelName order by r.createdAt desc")
    List<Review> findByHotelName(String hotelName);
}
