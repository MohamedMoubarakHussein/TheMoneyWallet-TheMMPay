package com.walletservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.walletservice.dto.request.WalletRequest;
import com.walletservice.entity.Transaction;
import com.walletservice.entity.Wallet;
import com.walletservice.repository.WalletRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public ResponseEntity<String> createWallet(WalletRequest wallet, long userId) {
        Wallet userWallet = Wallet.builder().CurrencyType(wallet.getCurrencyType())
                .balance(0)
                .userId(userId).build();

        ResponseEntity<String> response;
        try {
            userWallet = this.walletRepository.save(userWallet);
            response = new ResponseEntity<>(userWallet.toString(), HttpStatus.CREATED);
        } catch (Exception e) {
            String error = e.getMessage();
            response = new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

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


    }

}
