package com.themoneywallet.dashboardservice.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.themoneywallet.dashboardservice.entity.UserWallet;
import com.themoneywallet.dashboardservice.event.eventDTO.WalletEventDTo;


@Repository
public interface UserWalletsRepository extends JpaRepository<UserWallet, String> {
    
 
    List<UserWallet> findByUserId(String userId);
    
  
    Optional<UserWallet> findById(String walletId);
    
  
    Optional<UserWallet> findByUserIdAndIsPrimaryTrue(String userId);
    
   
    @Query("SELECT COALESCE(SUM(w.balance), 0) FROM UserWallet w WHERE w.userId = :userId")
    BigDecimal sumBalanceByUserId(@Param("userId") String userId);
    
   
    long countByUserId(String userId);
}