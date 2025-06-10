package com.themoneywallet.dashboardservice.service.implementation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.dashboardservice.dto.response.UnifiedResponse;
import com.themoneywallet.dashboardservice.dto.response.UserDashboardResponse;
import com.themoneywallet.dashboardservice.entity.UserDashboardSummary;
import com.themoneywallet.dashboardservice.entity.UserRecentTransaction;
import com.themoneywallet.dashboardservice.entity.UserWallet;
import com.themoneywallet.dashboardservice.entity.fixed.ResponseKey;
import com.themoneywallet.dashboardservice.event.eventDTO.WalletEventDTo;
import com.themoneywallet.dashboardservice.repository.UserDashboardRepository;
import com.themoneywallet.dashboardservice.repository.UserRecentTransactionsRepository;
import com.themoneywallet.dashboardservice.repository.UserWalletsRepository;
import com.themoneywallet.dashboardservice.utilities.UnifidResponseHandler;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
@Validated
public class DashboardQueryService {
    
    private final UserDashboardRepository dashboardRepository;
    private final UserRecentTransactionsRepository transactionsRepository;
    private final UserWalletsRepository walletsRepository;

     private final UnifidResponseHandler unifidHandler;
        private final ObjectMapper objectMapper;
           private final UnifidResponseHandler uResponseHandler;
    

    public  ResponseEntity<UnifiedResponse> getUserDashboard(@NotBlank String token , @NotBlank String refToken) {
        String userId;
        try {
            log.info("token before xascsa" + token);
            userId = this.getUserId(token, refToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                   
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of("message", "Internal error")
                                    ),
                                    true,
                                    "WA003"
                                )
                        
                );
        }
        // Get user summary - throws exception if user not found
         Optional<UserDashboardSummary> Opsummary = dashboardRepository.findByUserId(userId);
        UserDashboardSummary summary;
         if(Opsummary.isPresent()){
            summary = dashboardRepository.findByUserId(userId).get();
        }else{
            return null;
        }
         
        List<UserRecentTransaction> recentTransactions = 
            transactionsRepository.findTop10ByUserIdOrderByTransactionDateDesc(userId);
        
        List<UserWallet> wallets = walletsRepository.findByUserId(userId);
      

       UserDashboardResponse dashboardResponse =   UserDashboardResponse.builder()
                .userId(userId)
                .fullName(summary.getFullName())
                .email(summary.getEmail())
                .totalBalance(summary.getTotalBalance())
                .primaryWalletId(summary.getPrimaryWalletId())
                .recentTransactions(recentTransactions)
                .wallets(wallets)
                .lastUpdated(summary.getLastUpdated())
                .build();
          try {
            return new ResponseEntity<>(
                    this.unifidHandler.makResponse(
                            true,
                            this.unifidHandler.makeRespoData(
                                    ResponseKey.DATA,
                                    Map.of("dashboard" , 
                                    this.objectMapper.writeValueAsString(dashboardResponse)
                            )),
                            false,
                            "AUCR11001"
                        ),
                    HttpStatus.OK
                );
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
        
    }



    

    public ResponseEntity<UnifiedResponse> getUserSummary(@NotBlank String token,@NotBlank String refToken) {
                String id;
        try {
            id = this.getUserId(token, refToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                   
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of("message", "Internal error")
                                    ),
                                    true,
                                    "WA003"
                                )
                        
                );
        }
        
        try {
            return new ResponseEntity<>(
                    this.unifidHandler.makResponse(
                            true,
                            this.unifidHandler.makeRespoData(
                                    ResponseKey.DATA,
                                    Map.of("dashboard" , 
                                    this.objectMapper.writeValueAsString(dashboardRepository.findByUserId(id))
                            )),
                            false,
                            "AUCR11001"
                        ),
                    HttpStatus.OK
                );
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
   
            }
    


    public String getUserId(String token, String refToken) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserId'");
    }
}