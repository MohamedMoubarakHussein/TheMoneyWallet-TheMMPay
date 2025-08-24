package com.themoneywallet.dashboardservice.service.cache;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.dashboardservice.dto.response.UserDashboardResponse;
import com.themoneywallet.sharedUtilities.ports.CachePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisDashboardCacheService implements DashboardCacheService {

    private final CachePort cachePort;
    private final ObjectMapper objectMapper;

    private String key(String userId) {
        return "dashboard:" + userId;
    }

    @Override
    public void save(String userId, UserDashboardResponse response) {
        try {
            cachePort.put(key(userId), objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            log.error("Failed to save dashboard to cache for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public Optional<UserDashboardResponse> retrieve(String userId) {
        try {
            String json = cachePort.get(key(userId));
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, UserDashboardResponse.class));
        } catch (Exception e) {
            log.error("Failed to retrieve dashboard from cache for user {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void evict(String userId) {
        cachePort.delete(key(userId));
    }
}
