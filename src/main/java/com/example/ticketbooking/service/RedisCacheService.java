package com.example.ticketbooking.service;

import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> Optional<T> get(String cacheKey, Class<T> targetClass) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.convertValue(json, targetClass));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public <T> List<T> getList(String cacheKey, Class<T> targetClass) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        if (json == null) {
            return Collections.emptyList();
        }
        try {
            CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, targetClass);
            return objectMapper.readValue(json, collectionType);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public <T> void set(String cacheKey, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(cacheKey, json, ttl);
        } catch (Exception e) {
            throw new RedisException("Произошла ошибка с десериализацией обьекта");
        }
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }
}
