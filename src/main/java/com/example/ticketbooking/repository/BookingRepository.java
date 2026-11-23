package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Booking;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b" +
            " join Guest g on g.id = b.primaryGuest.id" +
            " where g.id = :guestId" +
            " and b.id = :bookingId")
    Optional<Booking> findByGuestId(@Param(value = "guestId") Long guestId,
                                    @Param(value = "bookingId") Long bookingId);

    @Query("select b from Booking b" +
            " join Guest g on g.id = b.primaryGuest.id" +
            " where g.id = :guestId" +
            " and b.checkIn >= :from" +
            " and b.checkOut <= :to")
    List<Booking> findAllByGuestId(@Param(value = "guestId") Long guestId,
                                   @Param(value = "from") LocalDateTime from,
                                   @Param(value = "to") LocalDateTime to,
                                   Limit limit,
                                   Sort sort);

    @Query("select b from Booking b" +
            " where b.hotel.id = :hotelId" +
            " and b.checkIn >= :checkIn" +
            " and b.checkOut <= :checkOut")
    List<Booking> findAllByHotelId(@Param(value = "hotelId") Long hotelId,
                                   @Param(value = "checkIn") LocalDateTime checkIn,
                                   @Param(value = "checkOut") LocalDateTime checkOut,
                                   Limit limit,
                                   Sort sort);

    @Query("select b from Booking b" +
            " where b.hotel.id = :hotelId" +
            " and b.room.id = :roomId" +
            " and b.checkIn < :checkOut" +
            " and b.checkOut > :checkIn" +
            " order by b.checkOut desc, b.checkIn desc" +
            " limit 1")
    Optional<Booking> findByHotelId(@Param("hotelId") Long hotelId,
                                    @Param("roomId") Long roomId,
                                    @Param("checkIn") LocalDateTime checkIn,
                                    @Param("checkOut") LocalDateTime checkOut);
}