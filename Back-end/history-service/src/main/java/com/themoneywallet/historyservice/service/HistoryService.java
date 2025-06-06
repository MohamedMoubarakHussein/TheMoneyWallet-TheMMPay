package com.themoneywallet.historyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.historyservice.entity.HistoryEvent;
import com.themoneywallet.historyservice.event.EventType;
import com.themoneywallet.historyservice.repository.HistoryEventRepository;
import com.themoneywallet.historyservice.service.shared.EventProducer;
import com.themoneywallet.historyservice.service.shared.RedisService;
import com.themoneywallet.historyservice.utilites.EventHandler;
import com.themoneywallet.historyservice.utilites.HttpHelper;
import com.themoneywallet.historyservice.utilites.UnifidResponseHandler;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryService {

    private final EventProducer eventProducer;
    private final UnifidResponseHandler uResponseHandler;
    private final EventHandler eventHandler;
    private final HttpHelper httpHelper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
    private final HistoryEventRepository hEventRepository;

    public List<HistoryEvent> getUserHistory(
        String userId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<EventType> eventTypes,
        int page,
        int size
    ) {
        List<HistoryEvent> events =
            this.hEventRepository.findByUserIdAndTimestampBetween(
                    userId,
                    startDate,
                    endDate,
                    page,
                    size
                );
        return events;
    }
}
