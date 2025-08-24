package com.themoneywallet.sharedUtilities.dto.request;


import java.math.BigDecimal;

import com.themoneywallet.sharedUtilities.enums.CurrencyType;
import com.themoneywallet.sharedUtilities.enums.ReceiverAddressType;

import lombok.Data;

@Data
public class TransferCreationRequest {

    private String id;

    
    private CurrencyType currencyType;

   
    private ReceiverAddressType receiverAddressType;

  
        private String receiverWallet;

    private String SenderWallet;
   
    private BigDecimal amount;

    private String message;
}
