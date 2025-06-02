package com.themoneywallet.historyservice.service.shared;

import com.themoneywallet.historyservice.entity.HistoryEvent;
import com.themoneywallet.historyservice.event.Event;
import com.themoneywallet.historyservice.repository.HistoryEventRepository;
import com.themoneywallet.historyservice.service.HistoryService;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class eventListener {

    private final HistoryService historyService;
    private final HistoryEventRepository historyEventRepository;

    @KafkaListener(
        topics = { "user-signup-event" },
        groupId = "history-service"
    )
    public void consumeEvents(Event event) {
        HistoryEvent hEvent = HistoryEvent.builder()
            .eventId(event.getEventId())
            .id(UUID.randomUUID().toString())
            .userId(event.getUserId())
            .eventType(event.getEventType())
            .serviceSource("add not now ")
            .eventData(event.getAdditionalData())
            .timestamp(event.getTimestamp())
            .build();

        this.historyEventRepository.save(hEvent);
    }
}
