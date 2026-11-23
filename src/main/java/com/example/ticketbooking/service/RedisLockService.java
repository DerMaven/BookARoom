package com.example.ticketbooking.service;

import com.example.ticketbooking.exception.BookingNotLockedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_ROOM = "lock:room";

    public Long acquireLock(Long roomId, Long bookingId, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<LocalDate> datesToLock = getDatesBetween(checkIn, checkOut);
        List<String> acquiredKeys = new ArrayList<>();

        try {
            for (LocalDate date: datesToLock) {
                String lockKey = LOCK_ROOM + ":" + roomId + ":" + date;
                boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(bookingId), Duration.ofMinutes(10));

                if (!locked) {
                    throw new BookingNotLockedException("Блокирование номера на эти даты не было осуществлено");
                }
                acquiredKeys.add(lockKey);
            }
        } catch (BookingNotLockedException e) {
            for (String lockKey : acquiredKeys) {
                redisTemplate.delete(lockKey);
            }
            throw e;
        }
        return bookingId;
    }


    public void releaseLock(Long roomId, Long bookingId, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<LocalDate> datesToUnlock = getDatesBetween(checkIn, checkOut);

        for (LocalDate date : datesToUnlock) {
            String lockKey = LOCK_ROOM + ":" + roomId + ":" + date;
            String currentOwner = redisTemplate.opsForValue().get(lockKey);

            if (String.valueOf(bookingId).equals(currentOwner)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    private List<LocalDate> getDatesBetween(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return List.of();
        }
        return checkIn.toLocalDate()
                .datesUntil(checkOut.toLocalDate())
                .toList();
    }
}
