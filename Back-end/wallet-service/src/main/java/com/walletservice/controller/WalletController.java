package com.walletservice.controller;

import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.utilities.ValidtionRequestHandler;
import com.walletservice.dto.request.CreateWalletRequestDTO;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.walletservice.dto.request.WalletUpdateRequest;
import com.walletservice.service.WalletService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;
    private final ValidtionRequestHandler validtionRequestHandlerhandler;


    @PostMapping("create")
    public ResponseEntity<UnifiedResponse> createWallet(@Valid @RequestBody CreateWalletRequestDTO request, BindingResult result, @RequestHeader("Authorization") String token) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.walletService.createWallet(request , token);
    }


    @GetMapping("/{walletId}")
    public ResponseEntity<UnifiedResponse> getWallet(@PathVariable  UUID walletId,@RequestHeader("Authorization") String token){
        return this.walletService.getWallet(walletId ,token );
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<UnifiedResponse> getUserWallets(@RequestHeader("Authorization") String token) {
        return this.walletService.getWallets(token);
    }
    
    @PutMapping("/{walletId}/balance")
    public ResponseEntity<UnifiedResponse> updateBalance(@Valid @RequestBody UpdateBalanceRequestDTO request,@RequestHeader("Authorization") String token, BindingResult result) { 
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.walletService.updateBalance(request,token);
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<UnifiedResponse> updateWallet(@Valid @RequestBody WalletUpdateRequest request,@RequestHeader("Authorization") String token, BindingResult result) { 
        if (result.hasErrors()) {
            return new ResponseEntity<>(
                this.validtionRequestHandlerhandler.handle(result),
                HttpStatus.BAD_REQUEST
            );
        }
        return this.walletService.updateWallet(request,token);
    }
    

}
