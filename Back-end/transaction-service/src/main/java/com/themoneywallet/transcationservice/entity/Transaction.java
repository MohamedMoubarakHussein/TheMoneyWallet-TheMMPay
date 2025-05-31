package com.themoneywallet.transcationservice.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String transferId;

    private String fromWalletId;
    private String toWalletId;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    private String description;
    private String idempotencyKey;
    private TransferStatus status;
    private LocalDateTime timestamp;

    @ElementCollection
    @CollectionTable(
        name = "matedata",
        joinColumns = @JoinColumn(name = "transferId")
    )
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "meatadata_value")
    private Map<String, String> metadata;
}
