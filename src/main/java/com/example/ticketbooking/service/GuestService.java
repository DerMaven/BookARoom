package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.GuestDto;
import com.example.ticketbooking.exception.GuestAlreadyExistsException;
import com.example.ticketbooking.exception.GuestCreationException;
import com.example.ticketbooking.exception.GuestNotFoundException;
import com.example.ticketbooking.model.Guest;
import com.example.ticketbooking.repository.GuestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final RedisCacheService redisCacheService;
    private final RedisLockService redisLockService;

    public GuestDto getById(Long guestId) {
        Optional<Guest> guestCache = redisCacheService.get(String.valueOf(guestId), Guest.class);
        if (guestCache.isPresent()) {
            Guest guest = guestCache.orElseThrow(() -> new GuestNotFoundException("Гость не был найден в кэше"));
            redisCacheService.set(String.valueOf(guestId), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
                    );
        }
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException("Гость не был найден в базе данных"));
        redisCacheService.set(String.valueOf(guest.getId()), guest, Duration.ofMinutes(10));
        return new GuestDto(
                guest.getId(),
                guest.getFirstName() + " " + guest.getLastName(),
                guest.getPhoneNumber(),
                guest.getEmail()
        );
    }

    public GuestDto getByEmail(String email) {
        Optional<Guest> guestCache = redisCacheService.get(String.valueOf(email), Guest.class);
        if (guestCache.isPresent()) {
            Guest guest = guestCache.orElseThrow(() -> new GuestNotFoundException("Гость не был найден в кэше"));
            redisCacheService.set(String.valueOf(email), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        }
        Guest guest = guestRepository.findByEmail(email)
                .orElseThrow(() -> new GuestNotFoundException("Гость не был найден в базе данных"));
        redisCacheService.set(String.valueOf(guest.getId()), guest, Duration.ofMinutes(10));
        return new GuestDto(
                guest.getId(),
                guest.getFirstName() + " " + guest.getLastName(),
                guest.getPhoneNumber(),
                guest.getEmail()
        );
    }

    public GuestDto getByPhoneNumber(String phoneNumber) {
        Optional<Guest> guestCache = redisCacheService.get(String.valueOf(phoneNumber), Guest.class);
        if (guestCache.isPresent()) {
            Guest guest = guestCache.orElseThrow(() -> new GuestNotFoundException("Гость не был найден в кэше"));
            redisCacheService.set(String.valueOf(phoneNumber), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        }
        Guest guest = guestRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new GuestNotFoundException("Гость не был найден в базе данных"));
        redisCacheService.set(String.valueOf(guest.getId()), guest, Duration.ofMinutes(10));
        return new GuestDto(
                guest.getId(),
                guest.getFirstName() + " " + guest.getLastName(),
                guest.getPhoneNumber(),
                guest.getEmail()
        );
    }

    public GuestDto createGuest(String firstName, String lastName, String email, String phoneNumber, Byte age, String passportNumber) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("createGuest");
        EntityManager em = emf.createEntityManager();

        Optional<Guest> guestEmailCache = redisCacheService.get(String.valueOf(email), Guest.class);
        if (guestEmailCache.isPresent()) {
            Guest guest = guestEmailCache.get();
            redisCacheService.set(String.valueOf(email), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        }

        Optional<Guest> guestPhoneCache = redisCacheService.get(String.valueOf(email), Guest.class);
        if (guestPhoneCache.isPresent()) {
            Guest guest = guestPhoneCache.get();
            redisCacheService.set(String.valueOf(email), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        }

        Optional<Guest> guestByEmail = guestRepository.findByEmail(email);
        if (guestByEmail.isPresent()) {
            throw new GuestAlreadyExistsException("Гость уже существует в базе данных");
        }

        Optional<Guest> guestByPhone = guestRepository.findByPhoneNumber(phoneNumber);
        if (guestByPhone.isPresent()) {
            throw new GuestAlreadyExistsException("Гость уже существует в базе данных");
        }

        try {
            em.getTransaction().begin();

            Guest guest = new Guest();
            guest.setFirstName(firstName);
            guest.setLastName(lastName);
            guest.setAge(age);
            guest.setEmail(email);
            guest.setPassportNumber(passportNumber);
            guest.setPhoneNumber(phoneNumber);

            em.persist(guest);
            em.flush();

            em.getTransaction().commit();

            redisCacheService.set(String.valueOf(guest.getId()), guest, Duration.ofMinutes(10));

            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new GuestCreationException("Возникла ошибка при создании гостя");
        } finally {
            em.close();
        }
    }

    public GuestDto updateGuest(String firstName, String lastName, String email, String phoneNumber, Byte age, String passportNumber) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("updateGuest");
        EntityManager em = emf.createEntityManager();

        Optional<Guest> guestEmailCache = redisCacheService.get(String.valueOf(email), Guest.class);
        if (guestEmailCache.isPresent()) {
            Guest guest = guestEmailCache.get();
            redisCacheService.set(String.valueOf(email), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        }

        Optional<Guest> guestPhoneCache = redisCacheService.get(String.valueOf(email), Guest.class);
        if (guestPhoneCache.isPresent()) {
            Guest guest = guestPhoneCache.get();
            redisCacheService.set(String.valueOf(email), guest, Duration.ofMinutes(5));
            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        }

        Optional<Guest> guestByEmail = guestRepository.findByEmail(email);
        if (guestByEmail.isPresent()) {
            throw new GuestAlreadyExistsException("Гость уже существует в базе данных");
        }

        Optional<Guest> guestByPhone = guestRepository.findByPhoneNumber(phoneNumber);
        if (guestByPhone.isPresent()) {
            throw new GuestAlreadyExistsException("Гость уже существует в базе данных");
        }

        try {
            em.getTransaction().begin();

            Guest guest = new Guest();
            guest.setFirstName(firstName);
            guest.setLastName(lastName);
            guest.setAge(age);
            guest.setEmail(email);
            guest.setPassportNumber(passportNumber);
            guest.setPhoneNumber(phoneNumber);

            em.persist(guest);
            em.flush();

            em.getTransaction().commit();

            redisCacheService.set(String.valueOf(guest.getId()), guest, Duration.ofMinutes(10));

            return new GuestDto(
                    guest.getId(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail()
            );
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new GuestCreationException("Возникла ошибка при создании гостя");
        } finally {
            em.close();
        }
    }
}
