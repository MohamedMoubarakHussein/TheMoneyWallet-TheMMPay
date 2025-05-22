package com.themoneywallet.historyservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.themoneywallet.historyservice.repository.HistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {
    
    private final HistoryRepository historyEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
 //   private final RedisTemplate<String, Object> redisTemplate;
    
    
    
/* 
    
    public HistoryResponse getUserHistory(String userId, HistoryFilter filter, Pageable pageable) {
       
        // Step 1: Check cache first for recent data
        String cacheKey = "user_history:" + userId + ":" + filter.hashCode();
        HistoryResponse cachedResponse = (HistoryResponse) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedResponse != null && filter.isRecentDataRequest()) {
            return cachedResponse;
        }
        
        // Step 2: Query database based on filter criteria
        List<HistoryEvent> events;
        if (filter.hasDateRange()) {
            events = historyEventRepository.findUserEventsByTypeAndDateRange(
                userId, filter.getEventTypes(), filter.getStartDate(), filter.getEndDate());
        } else {
            Page<HistoryEvent> eventPage = historyEventRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
            events = eventPage.getContent();
        }
        
        // Step 3: Transform to response DTOs
        List<HistoryEventResponse> eventResponses = events.stream()
            .map(this::transformToResponse)
            .collect(Collectors.toList());
        
        // Step 4: Create response with metadata
        HistoryResponse response = new HistoryResponse(eventResponses, events.size(), pageable.getPageNumber());
        
        // Step 5: Cache the response for future requests
        redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(15));
        
        return response;
    }
    
  
    
    private void cacheRecentEvent(HistoryEvent event) {
        String userCacheKey = "recent_events:" + event.getUserId();
        redisTemplate.opsForList().leftPush(userCacheKey, event);
        redisTemplate.opsForList().trim(userCacheKey, 0, 99); // Keep last 100 events
        redisTemplate.expire(userCacheKey, Duration.ofHours(24));
    }
    
    private void publishHistoryNotification(HistoryEvent event) {
        if (isNotificationWorthy(event)) {
            HistoryNotificationEvent notification = new HistoryNotificationEvent(
                event.getUserId(), event.getEventType(), event.getTimestamp());
            kafkaTemplate.send("history-notifications", notification);
        }
    }
        */
}
