package com.walletservice.entity;

import jakarta.persistence.CascadeType;
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
    private String id;

    @NotNull(message = "userId cannot be null.")
    private String userId;
    private String name;
    private boolean isPrimary;
    @DecimalMin(
        value = "0",
        message = "balance value must be greater than or equal to 0$."
    )
    @DecimalMax(
        value = "1000000.0000",
        message = "Maximum balance  must be less than or equal to one milion."
    )
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
    private String description;
    private LocalDateTime lastTransactionAt;

    @PrePersist
    public void setup() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
    }
}
