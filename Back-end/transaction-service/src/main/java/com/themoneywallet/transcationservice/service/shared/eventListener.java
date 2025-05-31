package com.themoneywallet.transcationservice.service.shared;

import com.themoneywallet.transcationservice.event.Event;
import com.themoneywallet.transcationservice.event.EventType;
import com.themoneywallet.transcationservice.repository.TransactionRepository;
import com.themoneywallet.transcationservice.service.TransactionService;
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

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @KafkaListener(
        topics = "WALLET_TRANSFER_COMPLETED",
        groupId = "transaction-service"
    )
    public void handleEvents(Event event) {}
}
