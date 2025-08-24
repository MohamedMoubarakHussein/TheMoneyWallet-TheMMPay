package com.themoneywallet.dashboardservice.service.cache;

import java.util.Optional;
import com.themoneywallet.dashboardservice.dto.response.UserDashboardResponse;

/**
 * Abstraction over dashboard caching mechanism.
 * Follows Dependency Inversion Principle by allowing higher-level components
 * to depend on this interface rather than a concrete Redis implementation.
 */
public interface DashboardCacheService {

    void save(String userId, UserDashboardResponse response);

    Optional<UserDashboardResponse> retrieve(String userId);

    void evict(String userId);
}
