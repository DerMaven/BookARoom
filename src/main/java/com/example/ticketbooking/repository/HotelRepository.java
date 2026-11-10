package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Hotel;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findAllBy(Limit limit, Sort sort);

    List<Hotel> findByCity(String city, Limit limit, Sort sort);

    @Query("select count(b.id) from Hotel h join Booking b on b.hotel.id = h.id where h.name = :name group by h.name")
    Long findVisitsByHotelName(String name);

    @Query("select h from Hotel h join Review r on r.hotel.id = h.id where lower(h.city) = lower(:city) group by h.id, h.name, r.stars order by count(r.id) desc limit :limit")
    List<Hotel> findTopNBestHotelsInCity(String city, Integer limit);
}
