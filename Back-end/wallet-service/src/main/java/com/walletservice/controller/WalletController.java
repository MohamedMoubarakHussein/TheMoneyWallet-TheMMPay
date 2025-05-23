package com.walletservice.controller;


import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.walletservice.dto.request.MakeTransactionUsingEmail;
import com.walletservice.dto.request.WalletCreationRequest;
import com.walletservice.entity.Transaction;
import com.walletservice.service.WalletService;
import com.walletservice.utilites.HttpHelper;
import com.walletservice.utilites.UnifidResponseHandler;
import com.walletservice.utilites.ValidationErrorMessageConverter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {
    private final WalletService walletService;
    private final ValidationErrorMessageConverter VErrorConverter;
    private final HttpHelper httpHelper;
    private final UnifidResponseHandler uResponseHandler;

    @PostMapping("/create")
    public ResponseEntity<String> createWallet(@Valid @RequestBody(required = false) WalletCreationRequest wallet, BindingResult result , @RequestHeader("Authorization") String token) {
       
        if(wallet == null){

            return ResponseEntity.badRequest()
            .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Required request body is missing"),true, "WA001").toString());
       }

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        }
           Long id;
        try {
             id  = getUserId(token);
        } catch (Exception e) {
            id = -1l;
        }
        
        if(id == -1){
               return ResponseEntity.badRequest()
            .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Internal error"),true, "WA003").toString());

        }
        
        return this.walletService.createWallet(wallet , id);
    }


     @GetMapping("/wallets")
    public ResponseEntity<String> getAllWallets(@RequestHeader("Authorization") String token) {

        
        return this.walletService.getAllWallets(getUserId(token));
    }

      @GetMapping("/get/{id}")
    public ResponseEntity<String> getWallet(@RequestHeader("Authorization") String token , @RequestParam("id") Long id) {

        
        return this.walletService.getWallet(getUserId(token) , id);
    }
/*
   

   
     @PostMapping("/status")
    public ResponseEntity<String> walletStatus(@Valid @RequestBody WalletRequest wallet, BindingResult result , @RequestHeader("Authorization") String token) {

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

     @GetMapping("/limits")
    public ResponseEntity<String> getWalletLimit( @RequestHeader("Authorization") String token, @RequestHeader("walletId") long wallet_Id) {
        long userId = getUserId(token);
        long WalletId = wallet_Id;
        return this.walletService.getBalanceExternal(userId , WalletId);
    }

     @PatchMapping("/updatelimit")
    public ResponseEntity<String> updateLimit(@Valid @RequestBody WalletRequest wallet, BindingResult result , @RequestHeader("Authorization") String token) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        }
        
        return this.walletService.createWallet(wallet , getUserId(token));
    }


     @PatchMapping("/addfunds")
    public ResponseEntity<String> addFunds(@Valid @RequestBody WalletRequest wallet, BindingResult result , @RequestHeader("Authorization") String token) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        }
        
        return this.walletService.createWallet(wallet , getUserId(token));
    }


     @PatchMapping("/rmfunds")
    public ResponseEntity<String> rmFunds(@Valid @RequestBody WalletRequest wallet, BindingResult result , @RequestHeader("Authorization") String token) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        }
        
        return this.walletService.createWallet(wallet , getUserId(token));
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

    */
    private long getUserId(String token) {
        ResponseEntity<String> req ; 
           try {
                req = this.httpHelper.sendDataToUserMangmentService(token);

           } catch (Exception e) {
                req = null;
                log.info(e.getMessage());
           }
            log.info(req.getStatusCode().toString());
        if(!req.getStatusCode().equals(HttpStatusCode.valueOf(200))){
            return -1;
        }
        return Long.valueOf(req.getBody());
    }

    

}
