package com.themoneywallet.dashboardservice.service.implementation;

import java.util.List;
import java.util.Optional;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.themoneywallet.dashboardservice.dto.response.UnifiedResponse;
import com.themoneywallet.dashboardservice.dto.response.UserDashboardResponse;
import com.themoneywallet.dashboardservice.entity.UserDashboardSummary;
import com.themoneywallet.dashboardservice.entity.UserNotification;
import com.themoneywallet.dashboardservice.entity.UserRecentTransaction;
import com.themoneywallet.dashboardservice.entity.UserWallet;
import com.themoneywallet.dashboardservice.repository.UserDashboardRepository;
import com.themoneywallet.dashboardservice.repository.UserNotificationRepository;
import com.themoneywallet.dashboardservice.repository.UserRecentTransactionsRepository;
import com.themoneywallet.dashboardservice.repository.UserWalletsRepository;
import com.themoneywallet.dashboardservice.utilities.UnifidResponseHandler;
import com.themoneywallet.dashboardservice.utilities.shared.JwtValidator;
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

    private final JwtValidator jwtValidator;
    private final UserDashboardRepository dashboardRepository;
    private final UserRecentTransactionsRepository transactionsRepository;
    private final UserWalletsRepository walletsRepository;
    private final UnifidResponseHandler uResponseHandler;
    private final UserNotificationRepository notificationRepository;
    private ResponseEntity<UnifiedResponse> successResponse;


    

    public  ResponseEntity<UnifiedResponse> getUserDashboard(@NotBlank String token   ) {
        String userId = this.getUserId(token.substring(7));
            System.out.println(userId+ " xz");
        if(userId == null){
            return this.uResponseHandler.generateFailedResponse("error", "Contact the admin of the site with the following code #DRTK00001", "DRTK00001" , "String");
        }

        Optional<UserDashboardSummary> Opsummary = dashboardRepository.findByUserId(userId);
        UserDashboardSummary summary;
          System.out.println(Opsummary.isPresent() + " xz2");
        if(Opsummary.isPresent()){
            summary = dashboardRepository.findByUserId(userId).get();
        }else{
            System.out.println(Opsummary.isPresent() + " xz2");
            return this.uResponseHandler.generateFailedResponse("error", "Contact the admin of the site with the following code #DRCR00001", "DRCR00001" , "String");
        }
            
        List<UserRecentTransaction> recentTransactions =  transactionsRepository.findTop10ByUserIdOrderByTransactionDateDesc(userId);

        List<UserWallet> wallets = walletsRepository.findByUserId(userId);
        log.info("  czv ");
        List<UserNotification> notifcations = this.notificationRepository.findByUserId(userId);
        log.info("  xzczv ");

        UserDashboardResponse dashboardResponse =   UserDashboardResponse.builder()
                .userId(userId)
                .fullName(summary.getFullName())
                .email(summary.getEmail())
                .totalBalance(summary.getTotalBalance())
                .primaryWalletId(summary.getPrimaryWalletId())
                .recentTransactions(recentTransactions)
                .userNotifications(notifcations)
                .wallets(wallets)
                .lastUpdated(summary.getLastUpdated())
                .build();
                        log.info("  xzczxxxx ");

                ResponseEntity<UnifiedResponse> successResponse2 = this.uResponseHandler.generateSuccessResponse("dashboard", dashboardResponse, HttpStatus.OK);
                System.out.println(successResponse2.toString()+" csz");
                return this.uResponseHandler.generateSuccessResponse("dashboard", dashboardResponse, HttpStatus.OK);

    }

    public String getUserId(String token ) {
        return this.jwtValidator.getUserId(token); 
    }
}