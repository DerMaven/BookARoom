package com.example.ticketbooking.repository;

import com.example.ticketbooking.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    @Query("select g from Guest g left join Booking b on b.primaryGuest.id = g.id where b.id is null")
    List<Guest> findUnregisteredGuests();

    @Query("select g from Guest g where g.email = :email")
    Optional<Guest> findByEmail(String email);

    @Query("select g from Guest g where g.phoneNumber = :phoneNumber")
    Optional<Guest> findByPhoneNumber(String phoneNumber);
}
