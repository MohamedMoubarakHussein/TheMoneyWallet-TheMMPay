package com.themoneywallet.transcationservice.dto.request;

import com.themoneywallet.transcationservice.entity.CurrencyType;
import com.themoneywallet.transcationservice.entity.ReceiverAddressType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferCreationRequest {

    private String id;

    @NotNull(message = "CurrencyType cannot be null.")
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @NotNull(message = "ReceiverAddressType cannot be null.")
    @Enumerated(EnumType.STRING)
    private ReceiverAddressType receiverAddressType;

    @NotNull(message = "wallet Reciver cannot be null")
    private String ReciverWallet;

    @NotNull(message = "wallet Reciver cannot be null")
    private String SenderWallet;

    @DecimalMin(
        value = "0",
        message = "amount value must be greater than or equal to 0$."
    )
    @DecimalMax(
        value = "1000000.0000",
        message = "Maximum amount  must be less than or equal to one milion."
    )
    @NotNull(message = "amount value cannot be null")
    private BigDecimal amount;

    private String message;
}
