package com.themoneywallet.historyservice.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.themoneywallet.historyservice.entity.History;

@Repository
public interface HistoryRepository extends CrudRepository<History, Long> {
    
    @Query("SELECT h FROM History h WHERE h.userId = :userId ORDER BY h.timestamp DESC")
    Page<History> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    
    
    
    @Query("SELECT h FROM History h WHERE h.userId = :userId AND h.eventType IN :eventTypes AND h.timestamp BETWEEN :startDate AND :endDate")
    List<History> findUserEventsByTypeAndDateRange(String userId, List<String> eventTypes, 
                                                       LocalDateTime startDate, LocalDateTime endDate);
    
   
}