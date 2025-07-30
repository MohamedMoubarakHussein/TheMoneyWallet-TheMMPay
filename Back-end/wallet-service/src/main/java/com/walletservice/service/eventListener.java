package com.walletservice.service;

import com.walletservice.repository.WalletRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class eventListener {

    private final WalletService walletService;
    private final WalletRepository walletRepository;
/* 
    // 2. Transaction Initiated Event - Reserve funds
    @KafkaListener(topics = "transaction.initiated", groupId = "wallet-service")
    // 3. Transaction Completed Event - Finalize balance updates
    @KafkaListener(topics = "transaction.completed", groupId = "wallet-service")
    // 4. Transaction Failed Event - Release reserved funds
    @KafkaListener(topics = "transaction.failed", groupId = "wallet-service")
*/

}
