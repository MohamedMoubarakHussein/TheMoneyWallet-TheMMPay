package com.themoneywallet.dashboardservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "User ID is required")
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @NotBlank(message = "Wallet ID is required")
    @Size(max = 255, message = "Wallet ID cannot exceed 255 characters")
    @Column(name = "wallet_id", nullable = false, unique = true)
    private String walletId;
    
    @NotBlank(message = "Wallet name is required")
    @Size(max = 255, message = "Wallet name cannot exceed 255 characters")
    @Column(name = "wallet_name", nullable = false)
    private String walletName;
    
    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Balance format is invalid")
    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;
    
    @NotBlank(message = "Wallet type is required")
    @Size(max = 50, message = "Wallet type cannot exceed 50 characters")
    @Pattern(regexp = "^(SAVINGS|CHECKING|INVESTMENT|BUSINESS)$",
             message = "Wallet type must be one of: SAVINGS, CHECKING, INVESTMENT, BUSINESS")
    @Column(name = "wallet_type", nullable = false)
    private String walletType;
    
    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
    
    @NotNull(message = "Last updated timestamp is required")
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;

        private LocalDateTime creationDate;
    

    @OneToOne(cascade = CascadeType.ALL)
    private WalletLimits limits;
    private String status;
        private Integer transactionCount;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}