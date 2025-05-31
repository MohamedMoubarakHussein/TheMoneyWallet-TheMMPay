package com.themoneywallet.notificationservice.service.shared;

import com.themoneywallet.notificationservice.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    public void publishWalletCreationEvent(Event event) {
        kafkaTemplate.send("wallet-creation-event", event);
        log.info("Published wallet event: {}", event);
    }

    public void publishWalletStatusChangedEvent(Event event) {
        kafkaTemplate.send("wallet-creation-event", event);
        log.info("Published wallet event: {}", event);
    }
}
