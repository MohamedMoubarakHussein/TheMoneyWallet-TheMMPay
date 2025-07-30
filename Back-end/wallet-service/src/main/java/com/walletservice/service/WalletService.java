package com.walletservice.service;

import com.themoneywallet.sharedUtilities.dto.event.Event;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.EventType;
import com.themoneywallet.sharedUtilities.enums.FixedInternalValues;
import com.themoneywallet.sharedUtilities.enums.WalletStatus;
import com.themoneywallet.sharedUtilities.utilities.EventHandler;
import com.themoneywallet.sharedUtilities.utilities.JwtValidator;
import com.themoneywallet.sharedUtilities.utilities.SerializationDeHalper;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.walletservice.dto.request.CreateWalletRequestDTO;
import com.walletservice.dto.request.UpdateBalanceRequestDTO;
import com.walletservice.dto.request.WalletUpdateRequest;
import com.walletservice.dto.response.BalanceUpdateResponseDTO;
import com.walletservice.dto.response.WalletResponseDTO;
import com.walletservice.entity.Wallet;
import com.walletservice.entity.WalletLimits;

import com.walletservice.repository.WalletRepository;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final EventProducer eventProducer;
    private final UnifidResponseHandler unifidHandler;
    private final EventHandler eventHandler;
    private final JwtValidator jwtService;
    private final SerializationDeHalper serializationDeHelper;

    private final WalletRepository walletRepository;
    private final BigDecimal DALIY_TRANSCATION_LIMIT = new BigDecimal(0);
    private final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal(0);
    private final BigDecimal MAX_BALANCE = new BigDecimal(0);
    private final BigDecimal MAX_TRANSCATION_AMOUNT = new BigDecimal(0);

   
    
    public ResponseEntity<UnifiedResponse> createWallet(CreateWalletRequestDTO request, String token) {
        UUID userId = this.jwtService.getUserId(token);
        if(walletRepository.existsByUserIdAndWalletTypeAndCurrency(userId, request.getWalletType(), request.getCurrency())) {
            return this.unifidHandler.generateFailedResponse("error", "User already has a wallet of this type and currency", "AUVD1003", "string" , HttpStatus.BAD_REQUEST);
        }
        
        
        Wallet wallet = Wallet.builder()
                        .userId(userId)
                        .walletId(UUID.randomUUID())
                        .walletType(request.getWalletType())
                        .currency(request.getCurrency())
                        .balance(BigDecimal.ZERO)
                        .availableBalance(BigDecimal.ZERO) 
                        .reservedBalance(BigDecimal.ZERO)
                        .status(WalletStatus.ACTIVE) 
                        .isPrimary(this.walletRepository.countByUserId(userId) == 0)
                        .transactionCount(0)
                        .limits(defauWalletLimits())
                        .lastTransactionAt(null) 
                        .build();

        
        Wallet savedWallet = walletRepository.save(wallet);
        if(!this.publishWalletCreationEvent(savedWallet)){
            return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
        }        
        return this.unifidHandler.generateSuccessResponse("data",savedWallet, HttpStatus.OK);
 
    }

    public WalletLimits defauWalletLimits(){
        return WalletLimits.builder()
                .dailyTransactionLimit(DALIY_TRANSCATION_LIMIT)
                .maxTransactionAmount(MAX_TRANSCATION_AMOUNT)
                .maxBalance(MAX_BALANCE)
                .lowBalanceThreshold(LOW_BALANCE_THRESHOLD)
                .build();
    }

    public boolean publishWalletCreationEvent(Wallet wallet){
        String data = this.serializationDeHelper.serailization(wallet);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.Wallet_CREATED,wallet.getUserId().toString(), "data", data);
            this.eventProducer.publishWalletCreationEvent(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public ResponseEntity<UnifiedResponse> getWallet(UUID walletId, String token) {
        UUID userId = this.jwtService.getUserId(token);
        Optional<Wallet> wallet = this.walletRepository.findByUserIdAndWalletId(userId, walletId);
        if(wallet.isPresent()){
            WalletResponseDTO respo = new WalletResponseDTO();
            BeanUtils.copyProperties(wallet.get(),respo);
            return this.unifidHandler.generateSuccessResponse("data",respo, HttpStatus.OK);

        }
        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<UnifiedResponse> getWallets(String token) {
        UUID userId = this.jwtService.getUserId(token);
        List<Wallet> wallets = this.walletRepository.findAllByUserId(userId);
        List<WalletResponseDTO> respo = new ArrayList<>();
        for(Wallet w : wallets){
            WalletResponseDTO respoz = new WalletResponseDTO();
            BeanUtils.copyProperties(w,respoz);
            respo.add(respoz);

        }
        return this.unifidHandler.generateSuccessResponse("data",respo, HttpStatus.OK);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<UnifiedResponse> updateBalance(UpdateBalanceRequestDTO request, String token) {

        UUID userId = this.jwtService.getUserId(token);
        Optional<Wallet> opWallet = this.walletRepository.findByUserIdAndWalletId(userId, request.getWalletId());
        if(opWallet.isPresent()){
            Wallet wallet = opWallet.get();
            if (wallet.getStatus() != WalletStatus.ACTIVE) {
                return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
            }

            switch (request.getOperationType()) {
                case CREDIT:
                    wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                    wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
                    break;
                case DEBIT:
                    if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
                    wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
                    break;
                    
                case RESERVE:
                    if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                        this.publishWalletBalanceReservedEvent(wallet , false);
                        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
                    wallet.setReservedBalance(wallet.getReservedBalance().add(request.getAmount()));
                    this.publishWalletBalanceReservedEvent(wallet , true);

                    break;
                    
                case RELEASE:
                    wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
                    wallet.setReservedBalance(wallet.getReservedBalance().subtract(request.getAmount()));
                    break;
            }
            wallet = walletRepository.save(wallet);
        
            this.publishWalletBalanceChangedEvent(wallet);
            BalanceUpdateResponseDTO respo = new BalanceUpdateResponseDTO();
            BeanUtils.copyProperties(opWallet.get(),respo);
            return this.unifidHandler.generateSuccessResponse("data",respo, HttpStatus.OK);

        }
        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean publishWalletBalanceChangedEvent(Wallet wallet){
        String data = this.serializationDeHelper.serailization(wallet);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.WALLET_BALANCE_CHANGED,wallet.getUserId().toString(), "data", data);
            this.eventProducer.publishWalletBalanceChangedEvent(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public ResponseEntity<UnifiedResponse> updateWallet(WalletUpdateRequest request, String token) {
         UUID userId = this.jwtService.getUserId(token);
        Optional<Wallet> wallet = this.walletRepository.findByUserIdAndWalletId(userId, request.getWalletId());
        if(wallet.isPresent()){
            Wallet respo = wallet.get();
            BeanUtils.copyProperties(request,respo);
            this.walletRepository.save(respo);
            if(!this.publishWalletChangedEvent(respo)){
                return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
            }        
            return this.unifidHandler.generateSuccessResponse("data",respo, HttpStatus.OK);

        }
        return this.unifidHandler.generateFailedResponse("error", "An unexpected error has occurred. Please contact our customer service team and provide the following error code: #AUPE0006. We apologize for the inconvenience.", "AUPE0006", "string" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

     public boolean publishWalletChangedEvent(Wallet wallet){
        String data = this.serializationDeHelper.serailization(wallet);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.WALLET_BALANCE_CHANGED,wallet.getUserId().toString(), "data", data);
            this.eventProducer.publishWalletStatusChangedEvent(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    // make dto waller reserved
     public boolean publishWalletBalanceReservedEvent(Wallet wallet , boolean status){
        String data = this.serializationDeHelper.serailization(wallet);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.WALLET_BALANCE_CHANGED,wallet.getUserId().toString(), "data", data);
            this.eventProducer.publishWalletTransactionReservedResultEvent(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

   
}
