package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.RatePlanDto;
import com.example.ticketbooking.exception.RatePlanNotFoundException;
import com.example.ticketbooking.model.RatePlan;
import com.example.ticketbooking.repository.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatePlanService {
    private final RatePlanRepository ratePlanRepository;
    private final RedisCacheService redisCacheService;

    public RatePlanDto getByHotelId(Long hotelId, Long ratePlanId) {
        Optional<RatePlan> planCache = redisCacheService.get(String.valueOf(ratePlanId), RatePlan.class);
        if (planCache.isPresent()) {
            RatePlan plan = planCache.orElseThrow(() -> new RatePlanNotFoundException("Тариф не был найден в кэше"));
            redisCacheService.set(String.valueOf(plan), plan, Duration.ofMinutes(10));
            return mapPlanToDto(plan);
        }
        RatePlan plan = ratePlanRepository.findByHotelId(hotelId, ratePlanId)
                .orElseThrow(() -> new RatePlanNotFoundException("Тариф не был найден в базе данных"));
        redisCacheService.set(String.valueOf(plan.getId()), plan, Duration.ofMinutes(10));
        return mapPlanToDto(plan);
    }

    public List<RatePlanDto> getByAllHotelId(Long hotelId, Long size) {
        List<RatePlan> plansCache = redisCacheService.getList(String.valueOf(hotelId), RatePlan.class);
        if (plansCache != null) {
            List<RatePlanDto> dtos = this.mapPlansToDtos(plansCache);
            redisCacheService.set(String.valueOf(hotelId), dtos, Duration.ofMinutes(10));
            return dtos;
        }
        List<RatePlan> plans = ratePlanRepository.findAllByHotelId(hotelId, size);
        if (plans == null) {
            throw new RatePlanNotFoundException("Ни один из тарифов не был найден у отеля");
        }
        redisCacheService.set(String.valueOf(hotelId), this.mapPlansToDtos(plans), Duration.ofMinutes(10));
        return this.mapPlansToDtos(plans);
    }

    private RatePlanDto mapPlanToDto(RatePlan plan) {
        return new RatePlanDto(
                plan.getId(),
                String.valueOf(plan.getMenuPlan()),
                plan.getPrice(),
                plan.getCurrency(),
                plan.getBeds(),
                plan.getMaxNightsStay(),
                plan.getIsActive()
        );
    }

    private List<RatePlanDto> mapPlansToDtos(List<RatePlan> plans) {
        return plans.stream()
                .map(this::mapPlanToDto)
                .toList();
    }
}
