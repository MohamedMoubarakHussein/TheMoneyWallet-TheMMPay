package com.walletservice.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walletservice.dto.request.MakeTransactionUsingEmail;
import com.walletservice.dto.request.WalletRequest;
import com.walletservice.entity.Transaction;
import com.walletservice.service.WalletService;
import com.walletservice.utilites.ValidationErrorMessageConverter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;
    private final ValidationErrorMessageConverter VErrorConverter;

    @PostMapping("/create")
    public ResponseEntity<String> createWallet(@Valid @RequestBody WalletRequest wallet, BindingResult result , @RequestHeader("Authorization") String token) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        }
        
        return this.walletService.createWallet(wallet , getUserId(token));
    }

    @GetMapping("/balance")
    public ResponseEntity<String> getBalanceExternal( @RequestHeader("Authorization") String token, @RequestHeader("walletId") long wallet_Id) {
        long userId = getUserId(token);
        long WalletId = wallet_Id;
        return this.walletService.getBalanceExternal(userId , WalletId);
    }

    public double getBalanceInternal(long user_Id, long wallet_Id) {

        return this.walletService.getBalanceInternal(user_Id , wallet_Id);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteWallet(@RequestHeader("Authorization") String token, @RequestHeader("walletId") long wallet_Id) {
        long userId = getUserId(token);
        long  walletId = wallet_Id;

        return this.walletService.deleteWallet(userId, walletId);

    }

    // sending using email address 
    @PostMapping("/transaction1")
    public ResponseEntity<String> makeTransactionUsingEmail(@Valid @RequestBody MakeTransactionUsingEmail request, BindingResult result , @RequestHeader("Authorization") String token, @RequestHeader("walletId") long wallet_Id){
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        }
        
        long SenderUserId = getUserId(token);
        long SenderWalletId = wallet_Id;
        double SenderBalance = getBalanceInternal(SenderUserId, SenderWalletId);
        Transaction transaction = Transaction.builder().CurrencyType(request.getCurrencyType())
                                            .SenderBalance(SenderBalance)
                                            .SenderUserId(SenderUserId)
                                            .SenderWalletId(SenderWalletId)
                                            .amount(request.getAmount()).build();

        long ReciverUserId = getUserIdFromEmail(request.getReciverEmail());
        return this.walletService.makeTransaction(transaction, ReciverUserId);
    }

    private long getUserIdFromEmail(String reciverEmail) {
        return 1;
    }

   
    private long getUserId(String token) {
        return 1;
    }

    

}
