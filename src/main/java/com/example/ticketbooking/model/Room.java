package com.example.ticketbooking.model;

import com.example.ticketbooking.model.enums.RoomStatus;
import com.example.ticketbooking.model.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jdk.jfr.BooleanFlag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms", indexes = {
        @Index(name = "idx_room_status", columnList = "status"),
        @Index(name = "idx_room_availability", columnList = "is_available"),
        @Index(name = "idx_room_max_occupancy", columnList = "max_occupancy")
})
@Getter
@Setter
@RequiredArgsConstructor
public class Room {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "room_number")
    @NotBlank(message = "You must enter room's number")
    private String roomNumber;

    @Column(name = "floor")
    private Long floor;

    @Column(name = "max_occupancy")
    @PositiveOrZero
    private Long maxOccupancy;

    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
