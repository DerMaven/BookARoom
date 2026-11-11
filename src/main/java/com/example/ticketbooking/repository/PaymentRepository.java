package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select t from Payment t join Booking b on t.booking.id = b.id join Guest g on b.primaryGuest.id = g.id where g.firstName = :firstName and g.lastName = :lastName order by t.processedAt desc")
    List<Payment> findByAllFullName(String firstName, String lastName);

    @Query("select t from Payment t join Hotel h on t.booking.hotel.id = h.id where h.id = :hotelId order by t.processedAt desc limit 1")
    Optional<Payment> findByHotelId(Long hotelId, Long paymentId);

    @Query("select t from Payment t join Booking b on t.booking.id = b.id")
    Optional<Payment> findByBookingId(Long bookingId);
}
