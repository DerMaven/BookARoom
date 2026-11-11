package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Booking;
import com.example.ticketbooking.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b join b.primaryGuest g where g.firstName = :firstName and g.lastName = :lastName")
    List<Booking> findByFullName(String firstName, String lastName);

    @Query("select b from Booking b join b.primaryGuest g where b.checkIn = :checkIn and g.firstName = :firstName and g.lastName = :lastName order by :checkIn desc")
    Optional<Booking> findByCheckInDateAndFullName(LocalDate checkIn, String firstName, String lastName);

    @Query("select b from Booking b join Hotel h on b.hotel.id = h.id join Room r on b.room.id = r.id where h.id = :hotelId and r.id = :roomId and b.checkIn = :checkIn and b.checkOut = :checkOut order by :checkOut desc, :checkIn desc")
    Optional<Booking> findByHotelIdAndRoomIdAndCheckInAndCheckOut(Long hotelId, Long roomId, LocalDate checkIn, LocalDate checkOut);

    @Query("select b from Booking b where b.bookingReference = :reference order by b.bookingReference")
    Optional<Booking> findByReference(String reference);
}