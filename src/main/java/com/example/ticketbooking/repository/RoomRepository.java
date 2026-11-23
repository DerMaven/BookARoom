package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Room;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "select r from Room r where r.hotel.id = :hotelId and r.id = :roomId")
    Optional<Room> findByHotel(@Param(value = "hotelId") Long hotelId,
                               @Param(value = "roomId") Long roomId);

    List<Room> findAllByHotelId(@Param(value = "hotelId") Long hotelId,
                                Limit limit,
                                Sort sort);

    boolean existsByIdAndHotelId(Long id,
                                 Long hotelId);
}
