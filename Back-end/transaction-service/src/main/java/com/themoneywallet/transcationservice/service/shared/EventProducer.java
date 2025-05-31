package com.themoneywallet.transcationservice.service.shared;

import com.themoneywallet.transcationservice.event.Event;
import com.themoneywallet.transcationservice.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    public void publishTransferInitiated(Event event) {
        kafkaTemplate.send(EventType.TRANSFER_TRANSACTION_START.name(), event);
        log.info("Published  event: {}", event);
    }
}
