package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select t from Payment t join Booking b on t.booking.id = b.id join Guest g on b.primaryGuest.id = g.id where g.firstName = :firstName and g.lastName = :lastName order by t.processedAt desc")
    List<Payment> findByAllFullName(String firstName, String lastName);

    @Query("select t from Payment t join Booking b on t.booking.id = b.id join Hotel h on b.hotel.id = h.id where h.currency = t.currency and h.name = :hotelName order by t.processedAt desc")
    List<Payment> findByAllHotelName(String hotelName);

    @Query("select t from Payment t join Booking b on t.booking.id = b.id")
    Optional<Payment> findByBookingId(Long bookingId);
}
