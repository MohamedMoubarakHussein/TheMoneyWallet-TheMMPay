package com.walletservice.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Entity
@Data
@Builder
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long walletId;

    @NotNull(message = "userId cannot be null.")
    private long userId;

    @DecimalMin(value = "0" , message = "balance value must be greater than or equal to 0$.")
    @DecimalMax(value = "1000000.0000" , message = "Maximum balance  must be less than or equal to one milion.")
    @NotNull(message = "balance value cannot be null")
    private double balance;

    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;

    private Date creationDate;

    @PrePersist
    private void prePersist(){
        this.creationDate = new Date();
    }
}
