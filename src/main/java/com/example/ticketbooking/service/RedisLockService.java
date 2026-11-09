package com.example.ticketbooking.service;

import com.example.ticketbooking.exception.BookingNotLockedException;
import com.example.ticketbooking.exception.ReleasingLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_ROOM = "lock:room";

    public Long acquireLock(Long roomId, Long bookingId, LocalDate checkIn, LocalDate checkOut) {
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


    public void releaseLock(Long roomId, Long bookingId, LocalDate checkIn, LocalDate checkOut) {
        List<LocalDate> datesToUnlock = getDatesBetween(checkIn, checkOut);
        List<String> acquiredKeys = new ArrayList<>();

        try {
            for (LocalDate date: datesToUnlock) {
                String unlockKey = LOCK_ROOM + ":" + roomId + ":" + date;
                boolean locked = redisTemplate.opsForValue().setIfAbsent(unlockKey, String.valueOf(bookingId), Duration.ofMinutes(10));
                if (!locked) {
                    throw new BookingNotLockedException("Блокирование номера на эти даты не было осуществлено");
                }
                acquiredKeys.add(unlockKey);
            }

            for (String lockKey: acquiredKeys) {
                redisTemplate.delete(lockKey);
            }
        } catch (ReleasingLockException e) {
            System.out.println("Возникла ошибка с разблокировкой значения, " + e.getMessage());
        }
    }

    private List<LocalDate> getDatesBetween(LocalDate checkIn, LocalDate checkOut) {
        return checkIn.datesUntil(checkOut).toList();
    }
}
