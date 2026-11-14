package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.RatePlanDto;
import com.example.ticketbooking.exception.RatePlanNotFoundException;
import com.example.ticketbooking.model.RatePlan;
import com.example.ticketbooking.repository.RatePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
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
        String cacheKey = "hotel:" + hotelId + ":rate-plan:" + ratePlanId;
        Optional<RatePlanDto> planCache = redisCacheService.get(cacheKey, RatePlanDto.class);
        if (planCache.isPresent()) {
            return planCache.get();
        }

        RatePlan plan = ratePlanRepository.findByHotelId(hotelId, ratePlanId)
                .orElseThrow(() -> new RatePlanNotFoundException("Тариф не был найден в базе данных"));

        RatePlanDto dto = mapPlanToDto(plan);
        redisCacheService.set(cacheKey, dto, Duration.ofMinutes(10));
        return dto;
    }

    public List<RatePlanDto> getByAllHotelId(Long hotelId,
                                             Integer size, String sortBy, String direction) {
        int limitSize = (size != null && size > 0) ? size : 10;
        String sortField = (sortBy != null) ? sortBy : "id";
        String sortDir = (direction != null) ? direction.toLowerCase() : "asc";


        List<RatePlan> plansCache = redisCacheService.getList(String.valueOf(hotelId), RatePlan.class);
        if (plansCache != null) {
            List<RatePlanDto> dtos = this.mapPlansToDtos(plansCache);
            redisCacheService.set(String.valueOf(hotelId), dtos, Duration.ofMinutes(10));
            return dtos;
        }

        Sort sort = createSort(sortField, sortDir);

        List<RatePlan> plans = ratePlanRepository.findAllByHotelId(hotelId, Limit.of(limitSize), sort);
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

    private Sort createSort(String sortBy, String direction) {
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Sort.Direction dirr = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dirr, field);
    }
}
