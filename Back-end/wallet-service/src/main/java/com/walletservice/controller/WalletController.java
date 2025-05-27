package com.walletservice.controller;


import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.walletservice.dto.request.WalletChangeFundReq;
import com.walletservice.dto.request.WalletCreationRequest;
import com.walletservice.entity.WalletLimits;
import com.walletservice.service.WalletService;
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
    private final UnifidResponseHandler uResponseHandler;




    @PostMapping("/create")
    public ResponseEntity<String> createWallet(@Valid @RequestBody(required = false) WalletCreationRequest wallet, BindingResult result , @RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken) {
       
        if(wallet == null)
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("data" , "Required request body is missing"),true, "WA001").toString());
       

        if (result.hasErrors())
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        
        Long id;

        try {
            id  = this.walletService.getUserId(token ,refToken);
        } catch (Exception e) {
                return ResponseEntity.badRequest()
                  .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Internal error"),true, "WA003").toString());
        }
        
        return this.walletService.createWallet(wallet , id);
    }



     @GetMapping("/wallets")
    public ResponseEntity<String> getAllWallets(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken) {

        return this.walletService.getAllWallets(this.walletService.getUserId(token ,refToken));
    }


    @GetMapping("/get")
    public ResponseEntity<String> getWallet(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {

        return this.walletService.getWallet(this.walletService.getUserId(token ,refToken) , id);
    }



    @GetMapping("/status")
    public ResponseEntity<String> walletStatus(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {

        return this.walletService.getWalletStaus(this.walletService.getUserId(token ,refToken) , id);
    }


    @PostMapping("/status")
    public ResponseEntity<String> walletStatusChange(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {
    
        return this.walletService.setWalletStaus(this.walletService.getUserId(token ,refToken) , id);
    }


    @GetMapping("/balance")
    public ResponseEntity<String> getWalletBlance(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {
        
        return this.walletService.getWalletBalance(this.walletService.getUserId(token ,refToken) , id);
    }



    @GetMapping("/limits")
    public ResponseEntity<String> getWalletLimits(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {

        return this.walletService.getWalletLimits(this.walletService.getUserId(token ,refToken) , id);
    }


     @PatchMapping("/limits")
     @PreAuthorize("@mySecurity.checkCustomAccess()")
    public ResponseEntity<String> updateWalletLimits(@Valid @RequestBody(required = false) WalletLimits wallet, BindingResult result , @RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {
       
        if(wallet == null)
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Required request body is missing"),true, "WA001").toString());
    

        if (result.hasErrors())
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        
        Long userId;
        String refCookie ;
        try {
             userId  = this.walletService.getUserId(token ,refToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Internal error"),true, "WA003").toString());
        }
        
        return this.walletService.UpdateWalletLimits(wallet , userId , id);
    }


    @PatchMapping("/addfund")
    public ResponseEntity<String> addWalletFunds(@Valid @RequestBody(required = false) WalletChangeFundReq req, BindingResult result , @RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {
       
        if(req == null)
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Required request body is missing"),true, "WA001").toString());
       

        if (result.hasErrors())
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        
        Long userId;
        try {
             userId  = this.walletService.getUserId(token ,refToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Internal error"),true, "WA003").toString());
        }
        
        return this.walletService.addfund(req , userId , id);
    }


     @PatchMapping("/rmfund")
    public ResponseEntity<String> rmWalletFunds(@Valid @RequestBody(required = false) WalletChangeFundReq req, BindingResult result ,  @RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {
       
        if(req == null)
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Required request body is missing"),true, "WA001").toString());
       

        if (result.hasErrors()) 
            return ResponseEntity.badRequest().body(this.VErrorConverter.Convert(result));
        
        Long userId;
        try {
            userId  = this.walletService.getUserId(token ,refToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
              .body(this.uResponseHandler.makResponse(true, Map.of("error" , "Internal error"),true, "WA003").toString());

        }
        
        return this.walletService.rmfund(req , userId , id);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> walletDelete(@RequestHeader("Authorization") String token , @CookieValue("refreshToken") String refToken , @RequestParam("id") Long id) {
        
        return this.walletService.Delete(this.walletService.getUserId(token ,refToken) , id);
    }


   
    

}
