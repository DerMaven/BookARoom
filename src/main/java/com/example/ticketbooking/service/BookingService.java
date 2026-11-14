package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.BookingDto;
import com.example.ticketbooking.dto.HotelDto;
import com.example.ticketbooking.dto.PaymentDto;
import com.example.ticketbooking.exception.*;
import com.example.ticketbooking.model.*;
import com.example.ticketbooking.model.enums.*;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.BookingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class  BookingService {
    private final BookingRepository bookingRepository;
    private final HotelService hotelService;
    private final RoomService roomService;
    private final RedisLockService redisLockService;
    private final RedisCacheService redisCacheService;

    @Qualifier("paymentService")
    private final MoneyService paymentService;

    @Qualifier("refundService")
    private final MoneyService refundService;
    private final InventoryService inventoryService;
    private final GuestService guestService;

    private static final List<String> ALLOWED_SORT_FIELDS = new ArrayList<>(List.of("id", "isAvailable", "checkIn", "checkOut", "totalAmount"));

    public BookingDto createBooking(Long hotelId, Long roomId, Long ratePlanId, Long primaryGuestId, List<Long> guestIds, PaymentMethod paymentMethod, LocalDate checkIn, LocalDate checkOut) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("createBooking");
        EntityManager em = emf.createEntityManager();

        Optional<Booking> foundByDate = bookingRepository.findByHotelId(hotelId, checkIn, checkOut);
        if (foundByDate.isPresent()) {
            throw new BookingAlreadyExistsException("Бронь на эти даты, в этом отеле, на этот номер, уже существует");
        }
        if (!roomService.belongsToHotel(hotelId, roomId)) {
            throw new RoomNotBelongsToHotelException("Комната не принадлежит к этому отелю");
        }
        if (!roomService.roomsRatePlanBelongsToHotel(hotelId, ratePlanId)) {
            throw new RatePlanNotBelongsToHotelException("Выбранный тариф не принадлежит к этому отелю");
        }

        String tempId = UUID.randomUUID().toString();
        redisLockService.acquireLock(roomId, Long.valueOf(tempId), checkIn, checkOut);

        try {
            em.getTransaction().begin();

            Booking booking = new Booking();
            booking.setStatus(BookingStatus.CREATED);
            booking.setCreatedAt(LocalDateTime.now());

            Guest primaryGuest = em.find(Guest.class, primaryGuestId);
            booking.setPrimaryGuest(primaryGuest);

            List<Guest> guests = guestIds.stream()
                    .map(guestService::getById)
                    .map(guestService::mapDtoToGuest)
                            .toList();
            if (guests.size() > booking.getRoom().getMaxOccupancy()) {
                throw new RoomMaxOccupancyException("Превышен порог заселения");
            }
            booking.setGuests(guests);

            Hotel hotel = em.find(Hotel.class, hotelId);
            booking.setHotel(hotel);

            em.persist(hotel);

            RatePlan ratePlan = em.find(RatePlan.class, ratePlanId);
            booking.setRatePlan(ratePlan);

            em.persist(ratePlan);

            Room room = em.find(Room.class, roomId);
            if (room.getStatus() == RoomStatus.OCCUPIED ||
                    room.getStatus() == RoomStatus.MAINTENANCE ||
                    room.getStatus() == RoomStatus.RESERVED ||
                    room.getIsAvailable() == false) {
                throw new RoomNotAvailableException("Комната недоступна");
            }
            booking.setRoom(room);

            em.persist(room);

            DailyInventory inventory = em.find(DailyInventory.class, inventoryService.getInventoryByHotelId(hotelId, checkIn, checkOut));

            em.persist(inventory);

            Payment payment = new Payment();
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaymentMethod(paymentMethod);

            em.persist(payment);

            if (inventory.getBookingStock() + 1 > inventory.getTotalStock()) {
                em.getTransaction().rollback();
            }
            inventory.setBookingStock(inventory.getBookingStock() + 1);

            Currency hotelCurrency = hotelService.getCurrency(hotelId);
            Currency ratePlanCurrency = Currency.valueOf(hotelService.getRatePlan(hotelId, ratePlanId).currency());
            if (!paymentService.isHotelAndRatePlanCurrenciesEqual(hotelCurrency, ratePlanCurrency)) {
                em.getTransaction().rollback();
            }

            Long nights = (long) getNights(checkIn, checkOut);
            if (ratePlan.getMinNightsStay() > nights || ratePlan.getMaxNightsStay() < nights) {
                throw new NightsLimitReachedException("Вы превысили границу ночеваний");
            }
            paymentService.setPriceByRatePlan(payment, ratePlanId, nights);

            room.setStatus(RoomStatus.RESERVED);
            room.setIsAvailable(false);

            payment.setProcessedAt(LocalDateTime.now());
            paymentService.paymentLayer(payment);
            payment.setStatus(PaymentStatus.PAID);

            booking.setStatus(BookingStatus.CONFIRMED);

            int hotelHash = Integer.parseInt(String.format("%3.3s", hotelId.hashCode()));
            int planHash = Integer.parseInt(String.format("%3.3s", ratePlanId.hashCode()));
            String bookingReference = "H-" + hotelHash + "P-" + planHash + "-" + UUID.randomUUID();
            booking.setBookingReference(bookingReference);

            booking.setCheckIn(checkIn);
            booking.setCheckOut(checkOut);

            em.persist(booking);

            em.flush();
            em.getTransaction().commit();

            redisCacheService.set("booking:" + booking.getId(), String.valueOf(booking.getId()), Duration.ofMinutes(2));
            return mapBookingToDto(booking, paymentService.mapPaymentToDto(payment));
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new BookingCreationException("Возникла ошибка при создании брони");
        } finally {
            em.close();
            redisLockService.releaseLock(roomId, Long.valueOf(tempId), checkIn, checkOut);
        }
    }

    public BookingDto cancelBooking(Long bookingId, Long guestId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("createBooking");
        EntityManager em = emf.createEntityManager();


        try {
            em.getTransaction().begin();

            Booking booking = em.find(Booking.class, bookingId);
            Guest guest = em.find(Guest.class, guestId);

            if (!(booking.getStatus() == BookingStatus.CONFIRMED)) {
                throw new BookingStatusException("Бронь не была подтверждена, чтобы ее отменять");
            }
            if (!(Objects.equals(guest.getId(), booking.getPrimaryGuest().getId()))) {
                throw new BookingNotBelongsToGuestException("Бронь не принадлежит гостю. чтобы он мог ее отменить");
            }

            DailyInventory inventory = inventoryService.getInventoryByHotelId(booking.getHotel().getId(), booking.getCheckIn(), booking.getCheckOut());
            if ((inventory.getBookingStock() + 1 < 0)) {
                throw new StockDecrementException("Декрементирование инвентаря невозможно");
            }

            inventory.setBookingStock(inventory.getBookingStock() - 1);

            em.persist(inventory);

            PaymentDto payment = refundService.getByBookingId(booking.getId());

            Payment refundPayment = new Payment();
            refundPayment.setAmount(payment.amount());
            refundPayment.setCurrency(payment.currency());
            refundPayment.setPaymentMethod(payment.paymentMethod());
            refundPayment.setStatus(PaymentStatus.REFUNDING);

            refundPayment.setProcessedAt(LocalDateTime.now());
            em.persist(refundPayment);

            refundService.paymentLayer(refundPayment);
            refundPayment.setStatus(PaymentStatus.REFUNDED);
            refundPayment.setBooking(booking);
            em.merge(refundPayment);

            booking.setStatus(BookingStatus.CANCELLED);
            em.merge(booking);

            em.flush();
            em.getTransaction().commit();

            return mapBookingToDto(booking, payment);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new BookingCancellationException("Возникла ошибка при отмене брони");
        } finally {
            em.close();
            redisCacheService.set("bookings:" + bookingId, String.valueOf(bookingId), Duration.ofMinutes(10));
        }
    }

    public List<BookingDto> getAllByHotelId(Long hotelId,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? Math.min(100, size) : 10;
        String sortField = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : "isAvailable";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";

        String cacheKey = "bookings:" + hotelId + ":" + startTime + ":" + endTime + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<BookingDto> cachedBookings = redisCacheService.getList(cacheKey, BookingDto.class);
        if (!cachedBookings.isEmpty()) {
            return cachedBookings;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Booking> bookings = bookingRepository.findAllByHotelId(hotelId,
                startTime, endTime,
                Limit.of(limitSize), sort);

        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> bookingIds = bookings.stream().map(Booking::getId).toList();

        Map<Long, PaymentDto> paymentsMap = paymentService.getPaymentsByBookingIds(bookingIds);

        List<BookingDto> dtos = bookings
                .stream()
                .map(booking -> mapBookingToDto(booking, paymentsMap.get(booking.getId())))
                .toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(10));
        }
        return dtos;
    }

    public BookingDto getByGuestId(Long guestId, Long bookingId) {
        Optional<BookingDto> bookingCache = redisCacheService.get("booking:" + bookingId + ":" + guestId, BookingDto.class);
        if (bookingCache.isPresent()) {
            return bookingCache.get();
        }

        Booking booking = bookingRepository.findByGuestId(guestId, bookingId)
                .orElseThrow(() -> new GuestNotFoundException("Бронь не была найдена в базе данных"));
        redisCacheService.set("booking:" + bookingId + ":" + guestId, booking, Duration.ofMinutes(20));

        PaymentDto payment = paymentService.getByBookingId(booking.getId());
        return mapBookingToDto(booking, payment);
    }

    public List<BookingDto> getAllByGuestId(Long guestId, LocalDateTime startTime, LocalDateTime endTime, Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? Math.min(100, size) : 10;
        String sortField = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : "isAvailable";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";

        String cacheKey = "bookings:guest:" + guestId + ":" + startTime + ":" + endTime + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;

        List<BookingDto> cachedBookings = redisCacheService.getList(cacheKey, BookingDto.class);
        if (!cachedBookings.isEmpty()) {
            return cachedBookings;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Booking> bookings = bookingRepository.findAllByGuestId(guestId,
                startTime, endTime,
                Limit.of(limitSize), sort);

        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> bookingIds = bookings.stream().map(Booking::getId).toList();

        Map<Long, PaymentDto> paymentsMap = paymentService.getPaymentsByBookingIds(bookingIds);

        List<BookingDto> dtos = bookings
                .stream()
                .map(booking -> mapBookingToDto(booking, paymentsMap.get(booking.getId())))
                .toList();

        if (!dtos.isEmpty()) {
            redisCacheService.set(cacheKey, dtos, Duration.ofMinutes(3));
        }
        return dtos;
    }

    public BookingDto getById(Long bookingId) {
        Optional<Booking> bookingCache = redisCacheService.get(String.valueOf(bookingId), Booking.class);
        if (bookingCache.isPresent()) {
            Booking booking = bookingCache.orElseThrow(() -> new BookingNotFoundException("Бронь не была найдена в кэше"));
            redisCacheService.set(String.valueOf(bookingId), booking, Duration.ofMinutes(5));
            PaymentDto payment = paymentService.getByBookingId(bookingId);
            return mapBookingToDto(booking, payment);
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new GuestNotFoundException("Бронь не была найдена в базе данных"));
        redisCacheService.set(String.valueOf(booking.getId()), booking, Duration.ofMinutes(20));
        PaymentDto payment = paymentService.getByBookingId(bookingId);
        return mapBookingToDto(booking, payment);
    }

    private BookingDto mapBookingToDto(Booking booking, PaymentDto payment) {
        return new BookingDto(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getRoom().getRoomType(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getNights().longValue(),
                booking.getRatePlan().getPrice().longValue(),
                payment.amount(),
                payment.currency(),
                true,
                booking.getRatePlan().getId()
        );
    }

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }

    private int getNights(LocalDate checkIn, LocalDate checkOut) {
        return checkIn.datesUntil(checkOut).toList().size();
    }
}
