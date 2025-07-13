package com.themoneywallet.dashboardservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import  com.themoneywallet.dashboardservice.entity.fixed.CurrencyType;
import com.themoneywallet.dashboardservice.entity.fixed.WalletStatus;
import com.themoneywallet.dashboardservice.entity.fixed.WalletTypes;

@Entity

@Table(name = "user_wallets",
       indexes = {
           @Index(name = "idx_user_wallet", columnList = "userId"),
           @Index(name = "idx_wallet_id", columnList = "walletId"),
           @Index(name = "idx_user_primary", columnList = "userId, isPrimary")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWallet {
    
    @Id
    private String id;
    
    @NotBlank(message = "User ID is required")
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    @Column(name = "user_id", nullable = false)
    private String userId;
    
  
    
 
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Balance format is invalid")
    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;
    

    
    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
 

  

    

    @OneToOne(cascade = CascadeType.ALL)
    private WalletLimits limits;
    private WalletStatus status;
        private Integer transactionCount;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String name;

    private WalletTypes type;


    private CurrencyType currency;


    private String description;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTransactionAt;

    @PrePersist
    public void setup(){
        if(this.id == null)
            this.id = UUID.randomUUID().toString();
    }
}