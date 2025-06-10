package com.themoneywallet.dashboardservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.themoneywallet.dashboardservice.entity.UserRecentTransaction;

@Repository
public interface UserRecentTransactionsRepository extends JpaRepository<UserRecentTransaction, Long> {
    
   
    List<UserRecentTransaction> findTop10ByUserIdOrderByTransactionDateDesc(String userId);
    
   
    List<UserRecentTransaction> findByUserIdOrderByTransactionDateDesc(String userId);
    
   
    long countByUserId(String userId);
  

    boolean existsByTransactionId(String transactionId);
}