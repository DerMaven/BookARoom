package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.HotelDto;
import com.example.ticketbooking.dto.PaymentDto;
import com.example.ticketbooking.dto.RatePlanDto;
import com.example.ticketbooking.exception.PaymentNotFoundException;
import com.example.ticketbooking.exception.RatePlanNotFoundException;
import com.example.ticketbooking.model.Hotel;
import com.example.ticketbooking.model.Payment;
import com.example.ticketbooking.model.RatePlan;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.RatePlanRepository;
import com.example.ticketbooking.repository.PaymentRepository;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("paymentService")
public class PaymentService extends MoneyService {
    private final RatePlanRepository ratePlanRepository;
    private final RedisCacheService redisCacheService;
    private final PaymentRepository paymentRepository;


    public PaymentService(RatePlanRepository ratePlanRepository, RedisCacheService redisCacheService, PaymentRepository paymentRepository) {
        super(ratePlanRepository, paymentRepository);
        this.redisCacheService = redisCacheService;
        this.ratePlanRepository = ratePlanRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String paymentLayer(Payment payment) {
        return "Платеж #" + payment.getId() + " на сумму " + payment.getAmount() +
                " " + payment.getCurrency() + " был успешно выполнен.";
    }

    @Override
    public boolean isHotelAndRatePlanCurrenciesEqual(Currency hotelCurrency, Currency ratePlanCurrency) {
        return Objects.equals(hotelCurrency, ratePlanCurrency);
    }

    public PaymentDto getByHotelId(Long hotelId, Long paymentId) {
        String cacheKey = "hotels:" + hotelId + ":" + paymentId;
        Optional<PaymentDto> paymentCache = redisCacheService.get(cacheKey, PaymentDto.class);
        if (paymentCache.isPresent()) {
            return paymentCache.get();
        }
        Payment payment = paymentRepository.findByHotelId(hotelId, paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж не был найден в базе данных"));

        PaymentDto dto = mapPaymentToDto(payment);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    public List<PaymentDto> getAllByHotelId(Long hotelId, LocalDateTime startTime, LocalDateTime endTime, Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? size : 10;
        String sortField = (sortBy != null) ? sortBy : "id";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";

        String cacheKey = "hotels:" + hotelId + ":" + startTime + ":" + endTime + ":sort:" + sortField + ":" + sortDir + ":size:" + limitSize;
        List<PaymentDto> paymentCache = redisCacheService.getList(cacheKey, PaymentDto.class);
        if (paymentCache != null) {
            return paymentCache;
        }

        Sort sort = createSort(sortField, sortDir);
        List<Payment> payments = paymentRepository.findAllByHotelId(hotelId, startTime, endTime, limitSize, sort);
        if (payments.isEmpty()) {
            return new ArrayList<>();
        }

        List<PaymentDto> dtos = mapPaymentsToDtos(payments);
        redisCacheService.set(cacheKey, payments, Duration.ofMinutes(10));
        return dtos;
    }

    public PaymentDto getByBookingId(Long bookingId) {
        String cacheKey = "bookings:" + bookingId;
        Optional<PaymentDto> paymentCache = redisCacheService.get(cacheKey, PaymentDto.class);

        if (paymentCache.isPresent()) {
            return paymentCache.get();
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("Платеж не был найден в базе данных"));

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

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }

    public PaymentDto mapPaymentToDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getProcessedAt(),
                payment.getPaymentMethod()
        );
    }

    private List<PaymentDto> mapPaymentsToDtos(List<Payment> payments) {
        return payments.stream().map(this::mapPaymentToDto).toList();
    }
}
