package com.themoneywallet.historyservice.service.shared;

import com.themoneywallet.historyservice.entity.HistoryEvent;
import com.themoneywallet.historyservice.event.Event;
import com.themoneywallet.historyservice.event.EventType;
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
        topicPattern = ".*",
        groupId = "history-service"
    )
    public void consumeEvents(Event event) {
        String sourceService = "Not avilable ";
        switch (event.getEventType()) {
            case EventType.AUTH_USER_SIGN_UP:
                sourceService = "Authentication Service";
                break;
            case EventType.AUTH_USER_LOGIN_SUCCESSED:
             sourceService = "Authentication Service";
                break;
        
            default:
                return;
                
        }
        HistoryEvent hEvent = HistoryEvent.builder()
            .eventId(event.getEventId())
            .id(UUID.randomUUID().toString())
            .userId(event.getUserId())
            .eventType(event.getEventType())
            .serviceSource(sourceService)
            .eventData(event.getAdditionalData())
            .timestamp(event.getTimestamp())
            .build();
    
        this.historyEventRepository.save(hEvent);
    }
}
