package com.themoneywallet.historyservice.entity;

import com.themoneywallet.historyservice.event.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HistoryEvent {

    private String id;

    private String eventId;

    private String userId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String serviceSource;

    private Map<String, Map<String, String>> eventData;

    private LocalDateTime timestamp;
}
