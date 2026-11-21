package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.GuestDto;
import com.example.ticketbooking.dto.UpdateGuestRequest;
import com.example.ticketbooking.exception.GuestAlreadyExistsException;
import com.example.ticketbooking.exception.GuestCreationException;
import com.example.ticketbooking.exception.GuestNotFoundException;
import com.example.ticketbooking.model.Guest;
import com.example.ticketbooking.repository.GuestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.MappingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {
    @PersistenceContext
    private EntityManager em;
    private final GuestRepository guestRepository;
    private final RedisCacheService redisCacheService;

    public GuestDto getById(Long guestId) {
        String cacheKey = "guest:" + guestId;
        Optional<GuestDto> guestCache = redisCacheService.get(cacheKey, GuestDto.class);
        if (guestCache.isPresent()) {
            return guestCache.get();
        }
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Гость не был найден в базе данных"));

        GuestDto dto = mapGuestToDto(guest);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    public GuestDto getByEmail(String email) {
        String cacheKey = "guest:email:" + email;
        Optional<GuestDto> guestCache = redisCacheService.get(cacheKey, GuestDto.class);
        if (guestCache.isPresent()) {
            return guestCache.get();
        }
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new GuestNotFoundException("Гость не был найден в базе данных"));

        GuestDto dto = mapGuestToDto(guest);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    public GuestDto getByPhoneNumber(String phoneNumber) {
        String cacheKey = "guest:phone:" + phoneNumber;
        Optional<GuestDto> guestCache = redisCacheService.get(cacheKey, GuestDto.class);
        if (guestCache.isPresent()) {
            return guestCache.get();
        }
        Guest guest = guestRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new GuestNotFoundException("Гость не был найден в базе данных"));

        GuestDto dto = mapGuestToDto(guest);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    @Transactional
    public GuestDto createGuest(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String passportNumber) {
        Optional<Guest> guestByEmail = guestRepository.findByEmail(email);
        if (guestByEmail.isPresent()) {
            throw new GuestAlreadyExistsException("Гость уже существует в базе данных");
        }

        Optional<Guest> guestByPhone = guestRepository.findByPhoneNumber(phoneNumber);
        if (guestByPhone.isPresent()) {
            throw new GuestAlreadyExistsException("Гость уже существует в базе данных");
        }

        Guest guest = new Guest();
        guest.setFirstName(firstName);
        guest.setLastName(lastName);
        guest.setBirthDate(birthDate);
        guest.setEmail(email);
        guest.setPassportNumber(passportNumber);
        guest.setPhoneNumber(phoneNumber);

        em.persist(guest);
        em.flush();

        GuestDto dto = mapGuestToDto(guest);
        redisCacheService.set("guest:" + guest.getId(), dto, Duration.ofMinutes(10));
        return dto;
    }

    @Transactional
    public GuestDto updateGuest(Long guestId, UpdateGuestRequest request) {
        String cacheKey = "guest:" + guestId;
        Optional<GuestDto> guestCache = redisCacheService.get(cacheKey, GuestDto.class);
        if (guestCache.isPresent()) {
            Guest guest = mapDtoToGuest(guestCache.get());

            if (request.firstName().isPresent()) guest.setFirstName(request.firstName().get());
            if (request.lastName().isPresent()) guest.setLastName(request.lastName().get());
            if (request.birthDate().isPresent()) guest.setBirthDate(request.birthDate().get());
            if (request.email().isPresent()) guest.setEmail(request.email().get());
            if (request.phoneNumber().isPresent()) guest.setPhoneNumber(request.phoneNumber().get());
            if (request.passportNumber().isPresent()) guest.setPassportNumber(request.passportNumber().get());
            redisCacheService.set(cacheKey, guest, Duration.ofMinutes(1));
            return mapGuestToDto(guest);
        }

        Guest guest = em.find(Guest.class, guestId);

        if (request.firstName().isPresent()) guest.setFirstName(request.firstName().get());
        if (request.lastName().isPresent()) guest.setLastName(request.lastName().get());
        if (request.birthDate().isPresent()) guest.setBirthDate(request.birthDate().get());
        if (request.email().isPresent()) guest.setEmail(request.email().get());
        if (request.phoneNumber().isPresent()) guest.setPhoneNumber(request.phoneNumber().get());
        if (request.passportNumber().isPresent()) guest.setPassportNumber(request.passportNumber().get());

        em.persist(guest);
        em.flush();

        redisCacheService.set(cacheKey, guest, Duration.ofMinutes(10));
        return mapGuestToDto(guest);
    }

    public Guest mapDtoToGuest(GuestDto dto) {
        return guestRepository.findById(dto.guestId())
                .orElseThrow(() -> new MappingException("Ошибка преобразования сущности"));
    }

    public GuestDto mapGuestToDto(Guest guest) {
        return new GuestDto(
                guest.getId(),
                guest.getFirstName() + " " + guest.getLastName(),
                guest.getPhoneNumber(),
                guest.getEmail()
        );
    }
}
