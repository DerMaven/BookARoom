package com.example.ticketbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "daily_inventories", indexes = {
        @Index(name = "idx_daily_inventories_total_stock", columnList = "total_stock"),
        @Index(name = "idx_daily_inventories_booking_stock", columnList = "booking_stock")
})
@Getter
@Setter
@RequiredArgsConstructor
public class DailyInventory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "inventory_date")
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime inventoryDate;

    @Column(name = "total_stock")
    @PositiveOrZero
    private Long totalStock;

    @Column(name = "booking_stock")
    @PositiveOrZero
    private Long bookingStock;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "daily_inventory_rooms",
            joinColumns = @JoinColumn(name = "daily_inventory_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private List<Room> rooms;
}
