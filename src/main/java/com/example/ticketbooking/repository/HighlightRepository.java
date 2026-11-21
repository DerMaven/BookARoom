package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {

    @Query("select hs from Highlight hs where hs.hotel.id = :hotelId order by hs.description")
    List<Highlight> findAllByHotelId(Long hotelId);
}
