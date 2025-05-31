package com.themoneywallet.transcationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.transcationservice.dto.request.TransferCreationRequest;
import com.themoneywallet.transcationservice.entity.Transaction;
import com.themoneywallet.transcationservice.entity.TransferStatus;
import com.themoneywallet.transcationservice.event.EventType;
import com.themoneywallet.transcationservice.repository.TransactionRepository;
import com.themoneywallet.transcationservice.service.shared.EventProducer;
import com.themoneywallet.transcationservice.service.shared.RedisService;
import com.themoneywallet.transcationservice.utilites.EventHandler;
import com.themoneywallet.transcationservice.utilites.HttpHelper;
import com.themoneywallet.transcationservice.utilites.UnifidResponseHandler;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final EventProducer eventProducer;
    private final UnifidResponseHandler uResponseHandler;
    private final EventHandler eventHandler;
    private final HttpHelper httpHelper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    private final TransactionRepository transactionRepository;

    public ResponseEntity<String> createTransfer(
        TransferCreationRequest transReq
    ) {
        Transaction transfer = Transaction.builder()
            .transferId(UUID.randomUUID().toString())
            .fromWalletId(transReq.getSenderWallet())
            .toWalletId(transReq.getReciverWallet())
            .amount(transReq.getAmount())
            .currency(transReq.getCurrencyType())
            .status(TransferStatus.INITIATED)
            .timestamp(LocalDateTime.now())
            .idempotencyKey(transReq.getId())
            .build();

        try {
            transfer = this.transactionRepository.save(transfer);
            this.eventProducer.publishTransferInitiated(
                    this.eventHandler.makeEvent(
                            EventType.TRANSFER_TRANSACTION_START,
                            String.valueOf(transfer.getFromWalletId()),
                            Map.of(
                                "data",
                                Map.of("transaction", transfer.toString())
                            )
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

    public ResponseEntity<String> getAllTransactions(String userId) {
        StringBuilder sb = new StringBuilder();

        for (Transaction we : this.transactionRepository.findAllByFromWalletId(
                userId
            )) {
            sb.append(we.toString() + " \n");
        }

        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of("data", Map.of("Transactions", sb.toString())),
                        false,
                        null
                    ).toString()
            );
    }

    public ResponseEntity<String> getTransaction(String userId, String id) {
        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of(
                                "Transaction",
                                this.transactionRepository.findByUserIdAndId(
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
}
