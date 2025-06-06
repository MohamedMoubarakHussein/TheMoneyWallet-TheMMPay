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
import com.walletservice.entity.fixed.ResponseKey;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/*
 *  return new ResponseEntity<>(
                this.objectMapper.writeValueAsString(
                        this.unifidHandler.makResponse(
                                true,
                                this.unifidHandler.makeRespoData(
                                        ResponseKey.ERROR,
                                        data
                                    ),
                                true,
                                "AUVD11002"
                            )
                    ),
                HttpStatus.BAD_REQUEST
            );
 */
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
        String userId
    ) throws JsonProcessingException {
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
                            Map.of(
                                ResponseKey.DATA.toString(),
                                Map.of(
                                    "data",
                                    this.objectMapper.writeValueAsString(
                                            userWallet
                                        )
                                )
                            )
                        )
                );
            return ResponseEntity.ok()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    false,
                                    null,
                                    false,
                                    null
                                )
                        )
                );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of("details", e.getMessage())
                                    ),
                                    true,
                                    "WA004"
                                )
                        )
                );
        }
    }

    public ResponseEntity<String> getAllWallets(String userId)
        throws JsonProcessingException {
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.DATA.toString(),
                                    Map.of(
                                        "Wallets",
                                        this.objectMapper.writeValueAsString(
                                                this.walletRepository.findAllByUserId(
                                                        userId
                                                    )
                                            )
                                    )
                                ),
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> getWallet(String userId, String id)
        throws JsonProcessingException {
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.DATA.toString(),
                                    Map.of(
                                        "wallet",
                                        this.objectMapper.writeValueAsString(
                                                this.walletRepository.findByUserIdAndId(
                                                        userId,
                                                        id
                                                    )
                                            )
                                    )
                                ),
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> getWalletStaus(String userId, String id)
        throws JsonProcessingException {
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.DATA.toString(),
                                    Map.of(
                                        "status",
                                        this.objectMapper.writeValueAsString(
                                                this.walletRepository.findByUserIdAndId(
                                                        userId,
                                                        id
                                                    ).getStatus()
                                            )
                                    )
                                ),
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> setWalletStaus(String userId, String id)
        throws JsonProcessingException {
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
                        Map.of(
                            ResponseKey.DATA.toString(),
                            Map.of(
                                "data",
                                this.objectMapper.writeValueAsString(wallet)
                            )
                        )
                    )
            );
        this.walletRepository.save(wallet);
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                false,
                                null,
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> getWalletBalance(String userId, String id)
        throws JsonProcessingException {
        log.info("user id :" + userId + " wallet id : " + id);
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.DATA.toString(),
                                    Map.of(
                                        "balance",
                                        this.objectMapper.writeValueAsString(
                                                this.walletRepository.findByUserIdAndId(
                                                        userId,
                                                        id
                                                    ).getBalance()
                                            )
                                    )
                                ),
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> getWalletLimits(String userId, String id)
        throws JsonProcessingException {
        log.info("user id :" + userId + " wallet id : " + id);
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                true,
                                Map.of(
                                    ResponseKey.DATA.toString(),
                                    Map.of(
                                        "limits",
                                        this.objectMapper.writeValueAsString(
                                                this.walletRepository.findByUserIdAndId(
                                                        userId,
                                                        id
                                                    ).getLimits()
                                            )
                                    )
                                ),
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> UpdateWalletLimits(
        WalletLimits walletLimit,
        String userId,
        String id
    ) throws JsonProcessingException {
        Wallet wallet = this.walletRepository.findByUserIdAndId(userId, id);
        wallet.setLimits(walletLimit);
        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_LIMIT_UPDATED,
                        userId,
                        Map.of(
                            ResponseKey.DATA.toString(),
                            Map.of(
                                "data",
                                this.objectMapper.writeValueAsString(wallet)
                            )
                        )
                    )
            );
        this.walletRepository.save(wallet);
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                false,
                                null,
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> addfund(
        WalletChangeFundReq req,
        String userId,
        String id
    ) throws JsonProcessingException {
        Wallet wallet = this.walletRepository.findByUserIdAndId(userId, id);
        wallet.setBalance(wallet.getBalance().add(req.getAmount()));
        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_ADD_FUND,
                        userId,
                        Map.of(
                            ResponseKey.DATA.toString(),
                            Map.of(
                                "data",
                                this.objectMapper.writeValueAsString(wallet)
                            )
                        )
                    )
            );
        this.walletRepository.save(wallet);
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                false,
                                null,
                                false,
                                null
                            )
                    )
            );
    }

    public ResponseEntity<String> rmfund(
        WalletChangeFundReq req,
        String userId,
        String id
    ) throws JsonProcessingException {
        Wallet wallet = this.walletRepository.findByUserIdAndId(userId, id);
        if (
            wallet.getBalance().compareTo(req.getAmount()) == 1 ||
            wallet.getBalance().compareTo(req.getAmount()) == 0
        ) {
            wallet.setBalance(wallet.getBalance().subtract(req.getAmount()));
            this.eventProducer.publishWalletStatusChangedEvent(
                    this.eventHandler.makeEvent(
                            EventType.WALLET_REMOVE_FUND,
                            userId,
                            Map.of(
                                ResponseKey.DATA.toString(),
                                Map.of(
                                    "data",
                                    this.objectMapper.writeValueAsString(wallet)
                                )
                            )
                        )
                );
            this.walletRepository.save(wallet);
            return ResponseEntity.ok()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    false,
                                    null,
                                    false,
                                    null
                                )
                        )
                );
        } else {
            return ResponseEntity.badRequest()
                .body(
                    this.objectMapper.writeValueAsString(
                            this.uResponseHandler.makResponse(
                                    true,
                                    Map.of(
                                        ResponseKey.ERROR.toString(),
                                        Map.of("message", "Insuffcient balance")
                                    ),
                                    true,
                                    null
                                )
                        )
                );
        }
    }

    public ResponseEntity<String> Delete(String userId, String walletId)
        throws JsonProcessingException {
        Wallet wallet =
            this.walletRepository.findByUserIdAndId(userId, walletId);
        this.walletRepository.delete(wallet);
        this.eventProducer.publishWalletStatusChangedEvent(
                this.eventHandler.makeEvent(
                        EventType.WALLET_DELETED,
                        userId,
                        Map.of(
                            ResponseKey.DATA.toString(),
                            Map.of(
                                "data",
                                this.objectMapper.writeValueAsString(wallet)
                            )
                        )
                    )
            );
        return ResponseEntity.ok()
            .body(
                this.objectMapper.writeValueAsString(
                        this.uResponseHandler.makResponse(
                                false,
                                null,
                                false,
                                null
                            )
                    )
            );
    }

    @Cacheable(value = "getUserId", key = "#refToken")
    public String getUserId(String token, String refToken) {
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

        return userId;
    }
}
