package com.themoneywallet.dashboardservice.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.dashboardservice.entity.UserDashboardSummary;
import com.themoneywallet.dashboardservice.entity.UserWallet;
import com.themoneywallet.dashboardservice.entity.fixed.CurrencyType;
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
    
  
    public void createUserDashboard(Event event) {
       
        Map<String, Map<String, String>> eventMap = event.getAdditionalData(); 
        UserEventDto user;
        try {
            user = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("profile"), UserEventDto.class);
        } catch (Exception e) {

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
       Map<String, Map<String, String>> eventMap = event.getAdditionalData(); 
       event.getAdditionalData().get(ResponseKey.DATA.toString()); 

       WalletEventDTo walletDTo ;
        try {
            walletDTo = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("profile"), WalletEventDTo.class);
        } catch (Exception e) {

            return;
        } 
      
        // Prevent duplicate wallets
        if (walletsRepository.findByWalletId(walletDTo.getId()).isPresent()) {
            log.warn("Wallet {} already exists, skipping creation", walletDTo.getId());
            return;
        }
        
        UserWallet  wallet = UserWallet.builder()
                .userId(walletDTo.getUserId())
                .walletId(walletDTo.getId())
                .balance(walletDTo.getBalance())
                .walletType(walletDTo.getWalletType())
                .lastUpdated(walletDTo.getUpdatedAt())
                .CurrencyType(CurrencyType.valueOf(walletDTo.getCurrencyType()))
                .creationDate(walletDTo.getCreationDate())
                .limits(walletDTo.getLimits())
                .status(walletDTo.getStatus())
                .transactionCount(walletDTo.getTransactionCount())
                .build();
        
        walletsRepository.save(wallet);
        
        updateUserTotalBalance(wallet.getUserId());
       
        
    }
     
    
    public void updateWalletBalance(Event event) {
       event.getAdditionalData().get(ResponseKey.DATA.toString()); 

       WalletEventDTo walletDTo ;
        try {
            walletDTo = this.objectMapper.readValue(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("profile"), WalletEventDTo.class);
        } catch (Exception e) {

            return;
        } 

        Optional<UserWallet> walletOpt = walletsRepository.findByWalletId(walletDTo.getId());
        
        if (walletOpt.isPresent()) {
            UserWallet wallet = walletOpt.get();
            wallet.setBalance(walletDTo.getBalance());
            wallet.setLastUpdated(LocalDateTime.now());
            
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
        Optional<UserWallet> walletOpt = walletsRepository.findByWalletId(walletDTo.getId());
        
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
 
}