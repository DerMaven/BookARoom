package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.PaymentDto;
import com.example.ticketbooking.exception.PaymentNotFoundException;
import com.example.ticketbooking.exception.RatePlanNotFoundException;
import com.example.ticketbooking.model.Payment;
import com.example.ticketbooking.model.RatePlan;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.RatePlanRepository;
import com.example.ticketbooking.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    @Override
    public PaymentDto getByBookingId(Long bookingId) {
        Optional<Payment> paymentCache = redisCacheService.get(String.valueOf(bookingId), Payment.class);
        if (paymentCache.isPresent()) {
            Payment payment = paymentCache.orElseThrow(() -> new PaymentNotFoundException("Оплата не была найден в кэше"));
            redisCacheService.set(String.valueOf(bookingId), payment, Duration.ofMinutes(5));
            return mapPaymentToDto(payment);
        }
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("Оплата не была найдена в базе данных"));
        redisCacheService.set(String.valueOf(payment.getBooking().getId()), payment, Duration.ofMinutes(15));
        return mapPaymentToDto(payment);
    }

    @Override
    public void setPriceByRatePlan(Payment payment, Long ratePlanId, Long nights) {
        RatePlan ratePlan = ratePlanRepository.findById(ratePlanId)
                .orElseThrow(() -> new RatePlanNotFoundException("Тариф не был найден"));
        Long amount = ratePlan.getPrice().longValue() * nights;
        payment.setAmount(amount);
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
}
