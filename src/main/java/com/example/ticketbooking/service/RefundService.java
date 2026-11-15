package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.PaymentDto;
import com.example.ticketbooking.dto.SortParams;
import com.example.ticketbooking.exception.*;
import com.example.ticketbooking.model.Payment;
import com.example.ticketbooking.model.RatePlan;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.model.enums.PaymentStatus;
import com.example.ticketbooking.repository.RatePlanRepository;
import com.example.ticketbooking.repository.PaymentRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("refundService")
public class RefundService extends MoneyService {
    private final RatePlanRepository ratePlanRepository;
    private final RedisCacheService redisCacheService;
    private final PaymentRepository paymentRepository;
    private static final List<String> ALLOWED_SORT_FIELDS = new ArrayList<>(List.of("id", "amount", "currency", "processedAt"));


    public RefundService(RatePlanRepository ratePlanRepository, RedisCacheService redisCacheService, PaymentRepository paymentRepository) {
        super(ratePlanRepository, paymentRepository);
        this.redisCacheService = redisCacheService;
        this.ratePlanRepository = ratePlanRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String paymentLayer(Payment payment) {
        return "Возврат #" + payment.getId() + " на сумму " + payment.getAmount() +
                " " + payment.getCurrency() + " был успешно выполнен.";
    }

    @Override
    public boolean isHotelAndRatePlanCurrenciesEqual(Currency hotelCurrency, Currency ratePlanCurrency) {
        return Objects.equals(hotelCurrency, ratePlanCurrency);
    }

    @Override
    public List<PaymentDto> getAllByHotelId(Long hotelId,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            SortParams params) {
        if (hotelId == null || hotelId <= 0) throw new InvalidHotelIdException("Указан неверный ID отеля");
        if (startTime == null || endTime == null || startTime.isAfter(endTime))
            throw new DateTimeException("Начало временного отрезка не может быть позже конца");

        int limitSize = (params.getSize() != null && params.getSize() > 0) ? Math.min(100, params.getSize()) : 10;
        String sortField = (params.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(params.getSortBy())) ? params.getSortBy() : "id";
        String sortDir = (params.getDirection() != null) ? params.getDirection().toLowerCase() : "asc";

        String cacheKey = "hotels:" + hotelId + ":" + startTime + ":" + endTime + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;
        List<PaymentDto> paymentCache = redisCacheService.getList(cacheKey, PaymentDto.class);
        if (!paymentCache.isEmpty()) {
            return paymentCache;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Payment> payments = paymentRepository.findAllByHotelId(hotelId,
                startTime, endTime,
                Limit.of(limitSize), sort)
                .stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.REFUNDED)
                .toList();
        if (payments.isEmpty()) {
            return new ArrayList<>();
        }

        List<PaymentDto> dtos = mapPaymentsToDtos(payments);
        redisCacheService.set(cacheKey, dtos, Duration.ofSeconds(50));
        return dtos;
    }

    @Override
    public PaymentDto getByBookingId(Long bookingId) {
        String cacheKey = "bookings:" + bookingId;
        Optional<PaymentDto> paymentCache = redisCacheService.get(cacheKey, PaymentDto.class);

        if (paymentCache.isPresent()) {
            return paymentCache.get();
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж не был найден в базе данных"));

        if (payment.getStatus() != PaymentStatus.REFUNDED) {
            throw new RefundTypeException("Тип транзакции не соответствует запросу");
        }

        PaymentDto dto = mapPaymentToDto(payment);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    @Override
    public void setPriceByRatePlan(Payment payment, Long ratePlanId, Long nights) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new RatePlanNotFoundException("Тариф не был найден"));
        Long amount = ratePlan.getPrice().longValue() * nights;
        payment.setAmount(amount);
    }

    @Override
    public Map<Long, PaymentDto> getPaymentsByBookingIds(List<Long> bookingIds) {
        return paymentRepository.findAllByBookingIdIn(bookingIds)
                .stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.REFUNDED)
                .collect(Collectors.toMap(
                        payment -> payment.getBooking().getId(),
                        this::mapPaymentToDto
                ));
    }

    @Override
    public List<PaymentDto> getRevenueRangeByHotelId(Long hotelId, Long rangeFrom, Long rangeTo, LocalDateTime startTime, LocalDateTime endTime, SortParams params) {
        if (hotelId == null || hotelId <= 0) throw new InvalidHotelIdException("Указан неверный ID отеля");
        if (rangeFrom == null || rangeTo == null || rangeFrom > rangeTo)
            throw new InvalidRevenueException("Начало диапазона не может быть больше конца");
        if (startTime == null || endTime == null || startTime.isAfter(endTime))
            throw new DateTimeException("Начало временного отрезка не может быть позже конца");

        int limitSize = (params.getSize() != null && params.getSize() > 0) ? Math.min(100, params.getSize()) : 10;
        String sortField = (params.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(params.getSortBy())) ? params.getSortBy() : "id";
        String sortDir = (params.getDirection() != null) ? params.getDirection().toLowerCase() : "asc";

        String cacheKey = "hotels:" + hotelId + ":" + rangeFrom + ":" + rangeTo + ":" + startTime + ":" + endTime + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;
        List<PaymentDto> paymentCache = redisCacheService.getList(cacheKey, PaymentDto.class);
        if (!paymentCache.isEmpty()) {
            return paymentCache;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Payment> payments = paymentRepository.findRevenueRangeByHotelId(hotelId,
                        rangeFrom, rangeTo,
                        startTime, endTime,
                        Limit.of(limitSize), sort)
                .stream()
                .filter(payment -> payment.getStatus() == PaymentStatus.REFUNDED)
                .toList();
        if (payments.isEmpty()) {
            return new ArrayList<>();
        }

        List<PaymentDto> dtos = mapPaymentsToDtos(payments);
        redisCacheService.set(cacheKey, dtos, Duration.ofSeconds(30));
        return dtos;
    }

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }

    @Override
    public PaymentDto mapPaymentToDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getProcessedAt(),
                payment.getPaymentMethod()
        );
    }

    @Override
    public List<PaymentDto> mapPaymentsToDtos(List<Payment> payments) {
        return payments.stream().map(this::mapPaymentToDto).toList();
    }
}
