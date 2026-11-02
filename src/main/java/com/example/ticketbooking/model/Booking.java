package com.example.ticketbooking.model;

import com.example.ticketbooking.dto.GuestDto;
import com.example.ticketbooking.model.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_bookings_check_in", columnList = "check_in"),
        @Index(name = "idx_bookings_check_out", columnList = "check_out"),
        @Index(name = "idx_bookings_status", columnList = "status")
})
@Getter
@Setter
@RequiredArgsConstructor
public class Booking {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "booking_reference")
    @NotBlank(message = "Field can't be empty")
    private String bookingReference;

    @Column(name = "check_in")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDate checkIn;

    @Column(name = "check_out")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDate checkOut;

    @Column(name = "nights")
    @PositiveOrZero
    private BigDecimal nights;

    @Column(name = "created_at")
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "primary_guest_id")
    private Guest primaryGuest;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "guests",
            joinColumns = { @JoinColumn(name = "guest_id") },
            inverseJoinColumns = { @JoinColumn(name = "booking_id") }
    )
    private List<Guest> guests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_plan_id")
    private RatePlan ratePlan;
}
