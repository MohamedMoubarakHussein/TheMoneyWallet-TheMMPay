package com.themoneywallet.dashboardservice.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.themoneywallet.dashboardservice.entity.UserNotification;
import com.themoneywallet.dashboardservice.entity.UserRecentTransaction;
import com.themoneywallet.dashboardservice.entity.UserWallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardResponse {
    
    @NotBlank
    private String userId;
    
    @NotBlank
    private String fullName;
    
    @Email
    @NotBlank
    private String email;
    
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal totalBalance;
    
    private String primaryWalletId;
    
    @NotNull
    private List<UserRecentTransaction> recentTransactions;

    @NotNull
    private List<UserNotification> userNotifications;
    
    @NotNull
    private List<UserWallet> wallets;
    
    @NotNull
    private LocalDateTime lastUpdated;
    
    private Integer walletsCount;
    private Integer recentTransactionsCount;
   
    
    @Override
    public String toString(){
        return "@NotBlank private String userId; @NotBlank private String fullName; @Email @NotBlank private String email; @NotNull @DecimalMin(\"0.0\") private BigDecimal totalBalance; private String primaryWalletId; @NotNull private List<UserRecentTransaction> recentTransactions; @NotNull private List<UserNotification> userNotifications; @NotNull private List<UserWallet> wallets;@NotNull private LocalDateTime lastUpdated;private Integer walletsCount;private Integer recentTransactionsCount;";                   
    }
  
            
}