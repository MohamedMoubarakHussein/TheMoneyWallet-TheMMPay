package com.themoneywallet.notificationservice.entity;

import com.themoneywallet.notificationservice.event.EventType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    private String userId;
    private EventType eventType;
    private String message;
    private LocalDateTime createdDate = LocalDateTime.now();
    private boolean read = false;
    private Map<String, Map<String, String>> metadata;
}
