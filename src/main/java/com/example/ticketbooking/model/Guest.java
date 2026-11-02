package com.example.ticketbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "guests", indexes = {
        @Index(name = "idx_guests_email", columnList = "email"),
        @Index(name = "idx_guests_first_name_last_name_birth_date", columnList = "first_name, last_name, birth_date")
})
@Getter
@Setter
@RequiredArgsConstructor
public class Guest {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "first_name")
    @NotBlank(message = "You must enter first name")
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "You must enter last name")
    private String lastName;

    @Column(name = "birth_date")
    @DateTimeFormat
    private LocalDate birthDate;

    @Column(name = "email")
    @Email(message = "You must enter a proper email")
    private String email;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "phone_number")
    @NotBlank(message = "Phone number has to be entered")
    private String phoneNumber;
}
