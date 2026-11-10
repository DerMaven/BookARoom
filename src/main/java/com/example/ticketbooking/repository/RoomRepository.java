package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Room;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select r from Room r where r.hotel.id = :hotelId order by r.status limit :size")
    List<Room> findAllByHotelId(Long hotelId, Long size);

    @Query(value = "select r from Room r where r.hotel.id = :hotelId order by r.status")
    Optional<Room> findByHotel(Long hotelId);

    List<Room> findAllByHotelId(Long hotelId, Limit limit, Sort sort);
}
