package com.walletservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.walletservice.dto.request.WalletCreationRequest;
import com.walletservice.dto.response.UnifiedResponse;
import com.walletservice.entity.CurrencyType;
import com.walletservice.entity.Transaction;
import com.walletservice.entity.Wallet;
import com.walletservice.entity.WalletLimits;
import com.walletservice.entity.WalletStatus;
import com.walletservice.entity.WalletTypes;
import com.walletservice.event.Event;
import com.walletservice.event.EventType;
import com.walletservice.repository.WalletRepository;
import com.walletservice.utilites.UnifidResponseHandler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final EventProducer eventProducer;
    private final UnifidResponseHandler uResponseHandler;

    private final WalletRepository walletRepository;
    private final BigDecimal DALIY_TRANSCATION_LIMIT = new BigDecimal(0);
    private final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal(0);
    private final BigDecimal MAX_BALANCE = new BigDecimal(0);
    private final BigDecimal MAX_TRANSCATION_AMOUNT = new BigDecimal(0);
    private final BigDecimal TEMP = new BigDecimal(0);

    
    public ResponseEntity<String> createWallet(WalletCreationRequest wallet, long userId) {
        
      
        UnifiedResponse unifiedResponse = new UnifiedResponse();
        Wallet userWallet =  new Wallet();
           userWallet.setUserId(userId);
           userWallet.setWalletType(wallet.getWalletType());
           userWallet.setStatus(WalletStatus.INACTIVE);
           userWallet.setCreationDate(Instant.now());
           userWallet.setUpdatedAt(Instant.now());
           userWallet.setCurrencyType(wallet.getCurrencyType());
           userWallet.setTransactionCount(0);
           userWallet.setLimits(WalletLimits.builder().dailyTransactionLimit(DALIY_TRANSCATION_LIMIT).lowBalanceThreshold(LOW_BALANCE_THRESHOLD).maxBalance(MAX_BALANCE).maxTransactionAmount(MAX_TRANSCATION_AMOUNT).build());
           userWallet.setBalance(BigDecimal.valueOf(0));
        

        try {
            userWallet = this.walletRepository.save(userWallet);

            
            Event event = new Event();
            event.setEventId(UUID.randomUUID().toString());
            event.setEventType(EventType.CREATED_WALLET);
            event.setTimestamp(LocalDateTime.now());
            event.setUserId(String.valueOf(userId));
            event.setAdditionalData(Map.of("data" , userWallet));
            this.eventProducer.publishWalletCreationEvent(event);
          return ResponseEntity.ok().body(this.uResponseHandler.makResponse(false, null, false, null).toString());
        } catch (Exception e) {
          

          return ResponseEntity.badRequest().body(  this.uResponseHandler.makResponse(true, Map.of("error", e.getMessage()), true, "WA004").toString());
            
        }
    }


    public ResponseEntity<String> getAllWallets(long userId) {
        StringBuilder sb = new StringBuilder();
        log.info(userId+"");
        for(Wallet we :this.walletRepository.findAllByUserId(userId)){
            sb.append(we.toString()+" \n");
            log.info(we.toString());
        }

        String allWallets = sb.toString();
     
        return ResponseEntity.ok().body(  this.uResponseHandler.makResponse(true, Map.of("data",allWallets), false, null).toString());
        
        
    }


    public ResponseEntity<String> getWallet(long userId, Long id) {
      return ResponseEntity.ok().body(  this.uResponseHandler.makResponse(true, Map.of("data",this.walletRepository.findById(id).get().toString()), false, null).toString());
    }

/*

    public ResponseEntity<String> getBalanceExternal(long userId, long wallet_Id) {
        Optional<Wallet> wallet = this.walletRepository.findById(wallet_Id);
        if (!wallet.isPresent()) {
            return new ResponseEntity<>("Wallet does not exist", HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("Wallet's balance is " + wallet.get().getBalance(), HttpStatus.OK);
    }

    public double getBalanceInternal(long user_Id, long wallet_Id) {
        Optional<Wallet> wallet = this.walletRepository.findById(wallet_Id);
        if (!wallet.isPresent()) {
            return 0;

        }
        return wallet.get().getBalance();
    }

    public ResponseEntity<String> deleteWallet(long userId, long walletId) {
        Optional<Wallet> wOptional = this.walletRepository.findById(walletId);
        if (!wOptional.isPresent()) {
            return new ResponseEntity<>("Wallet does not exist", HttpStatus.BAD_REQUEST);

        }
        if (wOptional.get().getUserId() == userId) {
            this.walletRepository.delete(wOptional.get());
        } else {
            return new ResponseEntity<>("Unsupported operation ", HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<>("Wallet with id " + walletId + " has been deleted successfully", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> makeTransaction(Transaction transaction, long reciverUserId) {
        // sender check
        Optional<Wallet> senderWallet = this.walletRepository.findById(transaction.getSenderWalletId());
        if (!senderWallet.isPresent() || senderWallet.get().getUserId() != transaction.getSenderUserId()) {
            return new ResponseEntity<>("Sender's wallet dosen't exist.", HttpStatus.BAD_REQUEST);
        }
        if (senderWallet.get().getCurrencyType() != transaction.getCurrencyType()) {
            return new ResponseEntity<>("Sender's wallet dosen't match currency type.", HttpStatus.BAD_REQUEST);
        }
        if (senderWallet.get().getBalance() < transaction.getAmount()) {
            return new ResponseEntity<>("Incefficent balance.", HttpStatus.BAD_REQUEST);
        }

        // reciver cheek
        List<Wallet> allReciverWallet = this.walletRepository.findAllByUserId(reciverUserId);
        Wallet reciverWallet = null;
        int found = 0;
        for(Wallet w : allReciverWallet){
            if(w.getCurrencyType() == transaction.getCurrencyType()){
                reciverWallet =w;
                found = 1;
                break;
            }
        }

        if(found == 0){
            return new ResponseEntity<>("Reciver's wallet doesn't exist.", HttpStatus.BAD_REQUEST);
        }

        // start the transaction
        reciverWallet.setBalance(transaction.getAmount()+reciverWallet.getBalance());
        Wallet sender = senderWallet.get();
        sender.setBalance(sender.getBalance()-transaction.getAmount());

        this.walletRepository.save(reciverWallet);
        this.walletRepository.save(sender);
        return new ResponseEntity<>(" Transaction has been completed.", HttpStatus.OK);


    }  */

}
