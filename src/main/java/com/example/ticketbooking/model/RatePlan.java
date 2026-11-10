package com.example.ticketbooking.model;

import com.example.ticketbooking.model.enums.MealMenuPlan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rate_plans", indexes = {
        @Index(name = "idx_rate_plans_currency", columnList = "currency"),
        @Index(name = "idx_rate_plans_price", columnList = "price")
})
@Getter
@Setter
@RequiredArgsConstructor
public class RatePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_plan")
    @Enumerated(EnumType.STRING)
    private MealMenuPlan menuPlan;

    @Column(name = "price")
    @PositiveOrZero
    private BigDecimal price;

    @Column(name = "beds")
    @PositiveOrZero
    private Long beds;

    @Column(name = "currency")
    @NotBlank
    private String currency;

    @Column(name = "min_nights_stay")
    @PositiveOrZero
    private Long minNightsStay;

    @Column(name = "max_nights_stay")
    @PositiveOrZero
    private Long maxNightsStay;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
