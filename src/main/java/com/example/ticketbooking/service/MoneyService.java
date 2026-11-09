package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.PaymentDto;
import com.example.ticketbooking.model.Payment;
import com.example.ticketbooking.model.enums.Currency;
import com.example.ticketbooking.repository.PaymentRepository;
import com.example.ticketbooking.repository.RatePlanRepository;
import org.springframework.stereotype.Service;

@Service
public abstract class MoneyService {
    private final RatePlanRepository ratePlanRepository;
    private final PaymentRepository paymentRepository;

    public MoneyService(RatePlanRepository ratePlanRepository, PaymentRepository paymentRepository) {
        this.ratePlanRepository = ratePlanRepository;
        this.paymentRepository = paymentRepository;
    }

    public abstract String paymentLayer(Payment payment);

    public abstract boolean isHotelAndRatePlanCurrenciesEqual(Currency hotelCurrency, Currency ratePlanCurrency);

    public abstract PaymentDto getByBookingId(Long bookingId);

    public abstract void setPriceByRatePlan(Payment payment, Long ratePlanId, Long nights);

    public abstract PaymentDto mapPaymentToDto(Payment payment);
}
