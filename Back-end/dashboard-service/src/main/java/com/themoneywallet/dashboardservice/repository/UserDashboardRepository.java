package com.themoneywallet.dashboardservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.themoneywallet.dashboardservice.entity.UserDashboardSummary;

@Repository
public interface UserDashboardRepository extends JpaRepository<UserDashboardSummary, String> {
    
 
    Optional<UserDashboardSummary> findByUserId(String userId);
  
    boolean existsByUserId(String userId);
}
