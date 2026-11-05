package com.example.ticketbooking.model;

import com.example.ticketbooking.model.enums.SurroundingCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "surroundings")
@RequiredArgsConstructor
@Getter
@Setter
public class Surrounding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Enumerated(value = EnumType.STRING)
    private SurroundingCategory category;

    private Double distance;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;
}
