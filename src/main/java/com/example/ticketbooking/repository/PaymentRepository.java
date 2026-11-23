package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Payment;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p join Booking b on p.booking.id = b.id")
    Optional<Payment> findByBookingId(Long bookingId);

    @Query("select p from Payment p join Hotel h on p.booking.hotel.id = :hotelId" +
            " where p.processedAt between :startTime and :endTime and h.id = :hotelId" +
            " order by :sort" +
            " limit :limit")
    List<Payment> findAllByHotelId(Long hotelId,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   Limit limit, Sort sort);

    @Query("select p from Payment p join Hotel h on p.booking.hotel.id = :hotelId" +
            " where p.processedAt between :startTime and :endTime" +
            " and h.id = :hotelId" +
            " order by :sort" +
            " limit :limit")
    List<Payment> findRevenueRangeByHotelId(Long hotelId,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            Limit limit, Sort sort);

    @Query("select p from Payment p where p.booking.id in :bookingIds")
    List<Payment> findAllByBookingIdIn(List<Long> bookingIds);

    @Query("select p from Payment p join Guest g on p.booking.primaryGuest.id = :guestId" +
            " where p.processedAt between :startTime and :endTime" +
            " and g.id = :guestId" +
            " order by :sort" +
            " limit :limit")
    List<Payment> findAllByGuestId(Long guestId, LocalDateTime startTime, LocalDateTime endTime, Limit of, Sort sort);
}
