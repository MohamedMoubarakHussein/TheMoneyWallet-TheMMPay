package com.walletservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.sharedUtilities.dto.event.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    
    private final KafkaTemplate<String, Event> kafkaTemplate;
    public void publishWalletCreationEvent(Event event){
        kafkaTemplate.send("Wallet-creation", event.getUserId(), event);
    }
    public void publishWalletBalanceChangedEvent(Event event){
        kafkaTemplate.send("Wallet-Balance-Update", event.getUserId(), event);
    }
    public void publishWalletTransactionReservedResultEvent(Event event){
        kafkaTemplate.send("Wallet-Reserved-Result", event.getUserId(), event);
    }

    public void publishWalletStatusChangedEvent(Event event){
        kafkaTemplate.send("Wallet-Changed", event.getUserId(), event);
    }

    



     
}