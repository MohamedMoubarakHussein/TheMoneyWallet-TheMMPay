package com.walletservice.service.shared;

import com.walletservice.entity.CurrencyType;
import com.walletservice.entity.Wallet;
import com.walletservice.entity.WalletLimits;
import com.walletservice.entity.WalletStatus;
import com.walletservice.entity.WalletTypes;
import com.walletservice.event.Event;
import com.walletservice.event.EventType;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.WalletService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class eventListener {

    private final WalletService walletService;
    private final WalletRepository walletRepository;

      @KafkaListener(topics = "user-signup-event", groupId = "wallet-service")
    public void handleEvent(Event event) {
        switch (event.getEventType()) {
            case EventType.AUTH_USER_LOGIN_SUCCESSED:
                    this.walletService.userLogin(event);
                break;
        
            default:
                break;
        }

    }
    @KafkaListener(topics = "user-signup-event", groupId = "wallet-service")
    public void handleEvents(Event event) {
        Wallet wallet = new Wallet();
        wallet.setUserId(event.getUserId());
        wallet.setWalletType(WalletTypes.PRIMARY);
        wallet.setStatus(WalletStatus.INACTIVE);
        wallet.setCreationDate(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        wallet.setCurrencyType(CurrencyType.POUND);
        wallet.setTransactionCount(0);
        wallet.setLimits(
            WalletLimits.builder()
                .dailyTransactionLimit(BigDecimal.valueOf(1000))
                .lowBalanceThreshold(BigDecimal.valueOf(1000))
                .maxBalance(BigDecimal.valueOf(2000))
                .maxTransactionAmount(BigDecimal.valueOf(200))
                .build()
        );
        wallet.setBalance(BigDecimal.valueOf(0));

        this.walletRepository.save(wallet);
    }
}
