package com.walletservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletservice.Exception.CannotGetUserIdFromUserServiceException;
import com.walletservice.dto.request.WalletChangeFundReq;
import com.walletservice.dto.request.WalletCreationRequest;
import com.walletservice.dto.response.UnifiedResponse;
import com.walletservice.entity.Wallet;
import com.walletservice.entity.WalletLimits;
import com.walletservice.entity.WalletStatus;
import com.walletservice.event.Event;
import com.walletservice.event.EventType;
import com.walletservice.repository.WalletRepository;
import com.walletservice.service.shared.EventProducer;
import com.walletservice.service.shared.RedisService;
import com.walletservice.utilites.EventHandler;
import com.walletservice.utilites.HttpHelper;
import com.walletservice.utilites.UnifidResponseHandler;
import io.jsonwebtoken.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final EventProducer eventProducer;
    private final UnifidResponseHandler uResponseHandler;
    private final EventHandler eventHandler;
    private final HttpHelper httpHelper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    private final WalletRepository walletRepository;
    private final BigDecimal DALIY_TRANSCATION_LIMIT = new BigDecimal(0);
    private final BigDecimal LOW_BALANCE_THRESHOLD = new BigDecimal(0);
    private final BigDecimal MAX_BALANCE = new BigDecimal(0);
    private final BigDecimal MAX_TRANSCATION_AMOUNT = new BigDecimal(0);
    private final BigDecimal TEMP = new BigDecimal(0);

    public ResponseEntity<String> createWallet(
        WalletCreationRequest wallet,
        long userId
    ) {
        Wallet userWallet = Wallet.builder()
            .userId(userId)
            .walletType(wallet.getWalletType())
            .status(WalletStatus.INACTIVE)
            .creationDate(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .CurrencyType(wallet.getCurrencyType())
            .transactionCount(0)
            .limits(
                WalletLimits.builder()
                    .dailyTransactionLimit(DALIY_TRANSCATION_LIMIT)
                    .lowBalanceThreshold(LOW_BALANCE_THRESHOLD)
                    .maxBalance(MAX_BALANCE)
                    .maxTransactionAmount(MAX_TRANSCATION_AMOUNT)
                    .build()
            )
            .balance(BigDecimal.valueOf(0))
            .build();

        try {
            userWallet = this.walletRepository.save(userWallet);
            this.eventProducer.publishWalletCreationEvent(
                    this.eventHandler.makeEvent(
                            EventType.CREATED_WALLET,
                            String.valueOf(userId),
                            Map.of("data", userWallet)
                        )
                );
            return ResponseEntity.ok()
                .body(
                    this.uResponseHandler.makResponse(
                            false,
                            null,
                            false,
                            null
                        ).toString()
                );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                    this.uResponseHandler.makResponse(
                            true,
                            Map.of("error", Map.of("message", e.getMessage())),
                            true,
                            "WA004"
                        ).toString()
                );
        }
    }

    public ResponseEntity<String> getAllWallets(long userId) {
        StringBuilder sb = new StringBuilder();

        for (Wallet we : this.walletRepository.findAllByUserId(userId)) {
            sb.append(we.toString() + " \n");
        }

        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of("data", Map.of("allWallet", sb.toString())),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> getWallet(long userId, Long id) {
        log.info("user id :" + userId + " wallet id : " + id);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "wallet",
                                this.walletRepository.findByUserIdAndId(
                                        userId,
                                        id
                                    ).toString()
                            )
                        ),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> getWalletStaus(long userId, Long id) {
        log.info("user id :" + userId + " wallet id : " + id);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "status",
                                this.walletRepository.findByUserIdAndId(
                                        userId,
                                        id
                                    )
                                    .getStatus()
                                    .toString()
                            )
                        ),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> setWalletStaus(long userId, Long id) {
        log.info("user id :" + userId + " wallet id : " + id);
        Wallet wallet = this.walletRepository.findByUserIdAndId(userId, id);
        if (wallet.getStatus().equals(WalletStatus.INACTIVE)) {
            wallet.setStatus(WalletStatus.ACTIVE);
        } else {
            wallet.setStatus(WalletStatus.INACTIVE);
        }

        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_STATUS_CHANGED,
                        String.valueOf(userId),
                        Map.of("data", wallet)
                    )
            );
        this.walletRepository.save(wallet);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        false,
                        null,
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> getWalletBalance(long userId, Long id) {
        log.info("user id :" + userId + " wallet id : " + id);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "balance",
                                this.walletRepository.findByUserIdAndId(
                                        userId,
                                        id
                                    )
                                    .getBalance()
                                    .toString()
                            )
                        ),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> getWalletLimits(long userId, Long id) {
        log.info("user id :" + userId + " wallet id : " + id);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "limits",
                                this.walletRepository.findByUserIdAndId(
                                        userId,
                                        id
                                    )
                                    .getLimits()
                                    .toString()
                            )
                        ),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> UpdateWalletLimits(
        WalletLimits walletLimit,
        Long userId,
        Long id
    ) {
        Wallet wallet =
            this.walletRepository.findByUserIdAndId(userId, Long.valueOf(id));
        wallet.setLimits(walletLimit);
        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_LIMIT_UPDATED,
                        String.valueOf(userId),
                        Map.of("data", wallet)
                    )
            );
        this.walletRepository.save(wallet);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        false,
                        null,
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> addfund(
        WalletChangeFundReq req,
        Long userId,
        Long id
    ) {
        Wallet wallet =
            this.walletRepository.findByUserIdAndId(userId, Long.valueOf(id));
        wallet.setBalance(wallet.getBalance().add(req.getAmount()));
        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_ADD_FUND,
                        String.valueOf(userId),
                        Map.of("data", wallet)
                    )
            );
        this.walletRepository.save(wallet);
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        false,
                        null,
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> rmfund(
        WalletChangeFundReq req,
        Long userId,
        Long id
    ) {
        Wallet wallet =
            this.walletRepository.findByUserIdAndId(userId, Long.valueOf(id));
        if (
            wallet.getBalance().compareTo(req.getAmount()) == 1 ||
            wallet.getBalance().compareTo(req.getAmount()) == 0
        ) {
            wallet.setBalance(wallet.getBalance().subtract(req.getAmount()));
            this.eventProducer.publishWalletStatusChangedEvent(
                    this.eventHandler.makeEvent(
                            EventType.WALLET_REMOVE_FUND,
                            String.valueOf(userId),
                            Map.of("data", wallet)
                        )
                );
            this.walletRepository.save(wallet);
            return ResponseEntity.ok()
                .body(
                    this.uResponseHandler.makResponse(
                            false,
                            null,
                            false,
                            null
                        ).toString()
                );
        } else {
            return ResponseEntity.badRequest()
                .body(
                    this.uResponseHandler.makResponse(
                            true,
                            Map.of(
                                "error",
                                Map.of("message", "Insuffcient balance")
                            ),
                            true,
                            null
                        ).toString()
                );
        }
    }

    public ResponseEntity<String> Delete(long userId, Long walletId) {
        Wallet wallet =
            this.walletRepository.findByUserIdAndId(
                    userId,
                    Long.valueOf(walletId)
                );
        this.walletRepository.delete(wallet);
        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_DELETED,
                        String.valueOf(userId),
                        Map.of("data", wallet)
                    )
            );
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        false,
                        null,
                        false,
                        null
                    ).toString()
            );
    }

    public long getUserId(String token, String refToken) {
        String key = this.redisService.getData(refToken);
        if (key != null) {
            log.info("Fetching userId from cache");
            return Long.valueOf(key);
        }

        ResponseEntity<String> req;
        log.info("Trying to get user id from user");
        req = this.httpHelper.sendDataToUserMangmentService(token, refToken);
        log.info("Finished getting respo from user service");
        if (
            req == null ||
            !req.getStatusCode().equals(HttpStatusCode.valueOf(200))
        ) throw new CannotGetUserIdFromUserServiceException();
        UnifiedResponse res;
        try {
            log.info("body   " + req.getBody());
            res = this.objectMapper.readValue(
                    req.getBody(),
                    UnifiedResponse.class
                );
        } catch (JsonMappingException e) {
            System.err.println("Mapping failed: " + e.getMessage());
            e.printStackTrace();
            throw new CannotGetUserIdFromUserServiceException();
        } catch (JsonProcessingException e) {
            System.err.println("Processing failed: " + e.getMessage());
            e.printStackTrace();
            throw new CannotGetUserIdFromUserServiceException();
        } catch (IOException e) {
            System.err.println("IO failed: " + e.getMessage());
            e.printStackTrace();
            throw new CannotGetUserIdFromUserServiceException();
        } catch (Exception e) {
            log.info("error convert into object");
            throw new CannotGetUserIdFromUserServiceException();
        }

        String userId = res.getData().get("data").get("id");

        try {
            this.redisService.saveData(refToken, userId);
        } catch (Exception e) {
            log.info("error in connecting to redis " + e.getMessage());
        }

        return Long.valueOf(userId);
    }
}
