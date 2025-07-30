package com.walletservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.themoneywallet.sharedUtilities.enums.CurrencyType;
import com.themoneywallet.sharedUtilities.enums.WalletStatus;
import com.themoneywallet.sharedUtilities.enums.WalletTypes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private UUID walletId;
    
    @Column(nullable = false)
    private UUID userId; 
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTypes walletType; 
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency; 
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal reservedBalance; 
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status; 
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;


  
    private boolean isPrimary;

    private Integer transactionCount;


    @OneToOne(cascade = CascadeType.ALL)
    private WalletLimits limits;

    private LocalDateTime lastTransactionAt;
    @Override
    public String toString() {
    return "Long:walletId;String:walletNumber;String:userId;WalletTypes:walletType;CurrencyType:currency;BigDecimal:balance;BigDecimal:availableBalance;BigDecimal:reservedBalance;WalletStatus:status;LocalDateTime:createdAt;LocalDateTime:updatedAt;boolean:isPrimary;Integer:transactionCount;WalletLimits:limits;String:description;LocalDateTime:lastTransactionAt;";
    }

   
}
