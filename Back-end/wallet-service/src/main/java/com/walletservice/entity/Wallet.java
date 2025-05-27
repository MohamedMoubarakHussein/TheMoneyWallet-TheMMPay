package com.walletservice.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet")
public class Wallet {

   
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     private long id;

    @NotNull(message = "userId cannot be null.")
     private long userId;

    @DecimalMin(value = "0" , message = "balance value must be greater than or equal to 0$.")
    @DecimalMax(value = "1000000.0000" , message = "Maximum balance  must be less than or equal to one milion.")
    @NotNull(message = "balance value cannot be null")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;
  
    private LocalDateTime creationDate;
    private LocalDateTime updatedAt;
    private Integer transactionCount;

    @Enumerated(EnumType.STRING)
    private WalletTypes walletType; 
    @OneToOne(cascade = CascadeType.ALL)
    private WalletLimits limits;
    private WalletStatus status; 

}
