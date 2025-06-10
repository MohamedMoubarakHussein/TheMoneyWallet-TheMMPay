package com.themoneywallet.dashboardservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_recent_transactions",
       indexes = {
           @Index(name = "idx_user_transaction_date", columnList = "userId, transactionDate"),
           @Index(name = "idx_transaction_id", columnList = "transactionId")
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecentTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "User ID is required")
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @NotBlank(message = "Transaction ID is required")
    @Size(max = 255, message = "Transaction ID cannot exceed 255 characters")
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;
    
    @NotNull(message = "Amount is required")
    @Digits(integer = 13, fraction = 2, message = "Amount format is invalid")
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @NotBlank(message = "Transaction type is required")
    @Size(max = 50, message = "Transaction type cannot exceed 50 characters")
    @Pattern(regexp = "^(CREDIT|DEBIT|TRANSFER|PAYMENT|REFUND)$", 
             message = "Transaction type must be one of: CREDIT, DEBIT, TRANSFER, PAYMENT, REFUND")
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @NotBlank(message = "Wallet ID is required")
    @Size(max = 255, message = "Wallet ID cannot exceed 255 characters")
    @Column(name = "wallet_id", nullable = false)
    private String walletId;
}