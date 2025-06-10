package com.themoneywallet.dashboardservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_dashboard_summary",
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_last_updated", columnList = "lastUpdated")
       })
@Data 
@Builder 
@NoArgsConstructor  
@AllArgsConstructor  
public class UserDashboardSummary {
    
    @Id
    @NotBlank(message = "User ID cannot be blank")
    @Size(max = 255, message = "User ID cannot exceed 255 characters")
    @Column(name = "user_id")
    private String userId;
    
    @NotBlank(message = "Full name is required")
    @Size(max = 255, message = "Full name cannot exceed 255 characters")
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Column(name = "email", nullable = false)
    private String email;
    
    @NotNull(message = "Total balance cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Total balance format is invalid")
    @Column(name = "total_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalBalance;
    
    @Size(max = 255, message = "Primary wallet ID cannot exceed 255 characters")
    @Column(name = "primary_wallet_id")
    private String primaryWalletId;
    
    @NotNull(message = "Last updated timestamp is required")
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}