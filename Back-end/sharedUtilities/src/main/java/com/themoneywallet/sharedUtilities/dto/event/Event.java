package com.themoneywallet.sharedUtilities.dto.event;

import java.time.LocalDateTime;
import java.util.Map;

import com.themoneywallet.sharedUtilities.enums.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    private String eventId;
    private String userId;
    private EventType eventType;
    private LocalDateTime timestamp;
    private Map<String, Map<String, String>> additionalData;
}
