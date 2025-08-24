package com.themoneywallet.dashboardservice.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.dashboardservice.entity.UserDashboardSummary;
import com.themoneywallet.dashboardservice.entity.UserWallet;
import com.themoneywallet.dashboardservice.entity.fixed.ResponseKey;
import com.themoneywallet.dashboardservice.event.Event;
import com.themoneywallet.dashboardservice.event.eventDTO.UserEventDto;
import com.themoneywallet.dashboardservice.event.eventDTO.WalletEventDTo;
import com.themoneywallet.dashboardservice.repository.UserDashboardRepository;
import com.themoneywallet.dashboardservice.repository.UserRecentTransactionsRepository;
import com.themoneywallet.dashboardservice.repository.UserWalletsRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional  
@RequiredArgsConstructor
@Slf4j  
@Validated 
public class DashboardDataService {
    
   
    private final UserDashboardRepository dashboardRepository;
    private final UserRecentTransactionsRepository transactionsRepository;
    private final UserWalletsRepository walletsRepository;
    private final ObjectMapper objectMapper;
    private final com.themoneywallet.dashboardservice.service.cache.DashboardCacheService cacheService;
    private final com.themoneywallet.dashboardservice.repository.UserNotificationRepository notificationRepository;
    
    
    public void createUserDashboard(Event event) {
       
        // Additional event data currently unused for creation logic
        UserEventDto user;
        try {
            user = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("data"), UserEventDto.class);
        } catch (Exception e) {
                log.info("csccccc  "+e.getMessage());
            return;
        } 
      


        if (dashboardRepository.existsByUserId(user.getId())) {
            log.info("Dashboard already exists for user: {}, skipping creation", event.getUserId());
            return;
        }
        
        
        UserDashboardSummary dashboard = UserDashboardSummary.builder()
                .userId(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .totalBalance(BigDecimal.ZERO)  
                .lastUpdated(LocalDateTime.now())
                .build();
        log.info("xsazz  " , dashboard.toString());
        this.dashboardRepository.save(dashboard);
    }




   
 /*
    public void updateUserInfo(Event event) {
       Map<String, Map<String, String>> eventMap = event.getAdditionalData(); 
        // my hatous ;)
        log.info(eventMap.get(ResponseKey.INFO.toString()).toString());
        Map<String, String> eventData = event.getAdditionalData().get(ResponseKey.DATA.toString()); 

        
        
        Optional<UserDashboardSummary> existingDashboard = 
            dashboardRepository.findByUserId(event.getUserId());
        
        if (existingDashboard.isPresent()) {
            UserDashboardSummary dashboard = existingDashboard.get();
            dashboard.setFullName(eventData.get("firstName"));
            dashboard.setEmail(eventData.get("firstName"));
            dashboard.setLastUpdated(LocalDateTime.now());
            
            dashboardRepository.save(dashboard);
            log.info("Updated user info for: {}", event.getUserId());
        } else {
            log.warn("Dashboard not found for user: {}, creating new one", event.getUserId());
            //createUserDashboard(event);
        }
    }
     */
 
  

    public void addUserWallet(Event event) {
       event.getAdditionalData().get(ResponseKey.DATA.toString()); 
        log.info("xxxaz  1");

       WalletEventDTo walletDTo ;
        try {
            log.info("cd  "+ event.getAdditionalData().get(ResponseKey.DATA.toString()).get("data"));
            walletDTo = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("data"), WalletEventDTo.class);
        } catch (Exception e) {
            log.info(e.getMessage());
            return;
        } 
        log.info("xxxaz  2");
        // Prevent duplicate wallets
        if (walletsRepository.findById(walletDTo.getId()).isPresent()) {
            log.warn("Wallet {} already exists, skipping creation", walletDTo.getId());
            return;
        }
                log.info("xxxaz  3");

        UserWallet  wallet = UserWallet.builder()
                .userId(walletDTo.getUserId())
                .id(walletDTo.getId())
                .balance(walletDTo.getBalance())
                .type(walletDTo.getType())
                .updatedAt(walletDTo.getUpdatedAt())
                .currency(walletDTo.getCurrency())
                .createdAt(walletDTo.getCreatedAt())
//                .limits(walletDTo.getLimits())
                .status(walletDTo.getStatus())
  //              .transactionCount(walletDTo.getTransactionCount())
                .build();
        
        walletsRepository.save(wallet);
                log.info("xxxaz  4");

        updateUserTotalBalance(wallet.getUserId());
               log.info("xxxaz  5");

        
    }
     
    
    public void updateWalletBalance(Event event) {
       event.getAdditionalData().get(ResponseKey.DATA.toString()); 

       WalletEventDTo walletDTo ;
        try {
            walletDTo = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("profile"), WalletEventDTo.class);
        } catch (Exception e) {

            return;
        } 

        Optional<UserWallet> walletOpt = walletsRepository.findById(walletDTo.getId());
        
        if (walletOpt.isPresent()) {
            UserWallet wallet = walletOpt.get();
            wallet.setBalance(walletDTo.getBalance());
            wallet.setUpdatedAt(LocalDateTime.now());
            
            walletsRepository.save(wallet);
            
            updateUserTotalBalance(walletDTo.getId());
            
         
        } 
    }
     
 
    public void removeUserWallet(Event event) {
 
       WalletEventDTo walletDTo ;
        try {
            walletDTo = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("profile"), WalletEventDTo.class);
        } catch (Exception e) {

            return;
        } 
        Optional<UserWallet> walletOpt = walletsRepository.findById(walletDTo.getId());
        
        if (walletOpt.isPresent()) {
            UserWallet wallet = walletOpt.get();
            
            walletsRepository.delete(wallet);
            
            updateUserTotalBalance(wallet.getUserId());
                      
        }
    }
     
/*
    public void addRecentTransaction(Event event) {
         Map<String, Map<String, String>> eventMap = event.getAdditionalData(); 
        // my hatous ;)
        log.info(eventMap.get(ResponseKey.INFO.toString()).toString());
        Map<String, String> eventData = event.getAdditionalData().get(ResponseKey.DATA); 

        // Prevent duplicate transactions
        if (transactionsRepository.existsByTransactionId(event.getTransactionId())) {
            log.warn("Transaction {} already exists, skipping", event.getTransactionId());
            return;
        }
        
        UserRecentTransaction transaction = UserRecentTransaction.builder()
                .userId(event.getUserId())
                .transactionId(event.getTransactionId())
                .amount(event.getAmount())
                .transactionType(event.getTransactionType())
                .description(event.getDescription())
                .transactionDate(event.getTransactionDate() != null ? 
                    event.getTransactionDate() : LocalDateTime.now())
                .walletId(event.getWalletId())
                .build();
        
        transactionsRepository.save(transaction);
        
    }
 
   */

private void updateUserTotalBalance( String id) {
 
        List<UserWallet> userWallets = walletsRepository.findByUserId(id);
        
        BigDecimal totalBalance = userWallets.stream()
                .map(UserWallet::getBalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Optional<UserDashboardSummary> dashboardOpt = dashboardRepository.findByUserId(id);
        
        if (dashboardOpt.isPresent()) {
            UserDashboardSummary dashboard = dashboardOpt.get();
            dashboard.setTotalBalance(totalBalance);
            dashboard.setLastUpdated(LocalDateTime.now());
            
            dashboardRepository.save(dashboard);
        } 
  
}

public void userLogin(Event event){
    String userId = event.getUserId();
    if(userId == null){
        return;
    }

    try {
        java.util.Optional<com.themoneywallet.dashboardservice.entity.UserDashboardSummary> summaryOpt = dashboardRepository.findByUserId(userId);
        if(summaryOpt.isEmpty()){
            // Dashboard not found – nothing to cache yet
            return;
        }

        java.util.List<com.themoneywallet.dashboardservice.entity.UserRecentTransaction> recentTransactions =
                transactionsRepository.findTop10ByUserIdOrderByTransactionDateDesc(userId);

        java.util.List<com.themoneywallet.dashboardservice.entity.UserWallet> wallets = walletsRepository.findByUserId(userId);

        java.util.List<com.themoneywallet.dashboardservice.entity.UserNotification> notifications = notificationRepository.findByUserId(userId);

        com.themoneywallet.dashboardservice.dto.response.UserDashboardResponse dashboardResponse =
                com.themoneywallet.dashboardservice.dto.response.UserDashboardResponse.builder()
                        .userId(userId)
                        .fullName(summaryOpt.get().getFullName())
                        .email(summaryOpt.get().getEmail())
                        .totalBalance(summaryOpt.get().getTotalBalance())
                        .primaryWalletId(summaryOpt.get().getPrimaryWalletId())
                        .recentTransactions(recentTransactions)
                        .userNotifications(notifications)
                        .wallets(wallets)
                        .lastUpdated(summaryOpt.get().getLastUpdated())
                        .build();

        cacheService.save(userId, dashboardResponse);
        log.info("Cached dashboard for user {}", userId);

    } catch (Exception e){
        log.error("Failed to cache dashboard on login for user {} : {}", userId, e.getMessage());
    }
}
 
}