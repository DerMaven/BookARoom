package com.example.ticketbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "highlights")
@Getter
@Setter
@RequiredArgsConstructor
public class Highlight {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "description")
    @NotBlank(message = "You must enter hotel's description")
    private String description;

    @Column(name = "icon")
    @NotBlank(message = "You must enter icon's description")
    private String iconURL;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
