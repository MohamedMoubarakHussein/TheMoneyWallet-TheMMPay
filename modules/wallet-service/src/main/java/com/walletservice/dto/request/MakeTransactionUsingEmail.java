package com.walletservice.dto.request;

import com.walletservice.entity.CurrencyType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MakeTransactionUsingEmail {
    @DecimalMin(value = "1" , message = "balance value must be greater than or equal to 0$.")
    @DecimalMax(value = "1000000.0000" , message = "Maximum balance  must be less than or equal to one milion.")
    @NotNull(message = "amount value cannot be null")
    private double amount;

    @Enumerated(EnumType.STRING)
    private CurrencyType CurrencyType;

    @Email(message = "Please Entre a valid email address. ")
    private String ReciverEmail;
}
