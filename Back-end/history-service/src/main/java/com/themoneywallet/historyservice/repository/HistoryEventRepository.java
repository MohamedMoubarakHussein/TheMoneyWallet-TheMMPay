package com.themoneywallet.historyservice.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.historyservice.entity.HistoryEvent;
import com.themoneywallet.historyservice.event.EventType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HistoryEventRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public void save(HistoryEvent event) {
        String sql =
            "INSERT INTO history_event " +
            "(id, event_id, user_id, event_type, service_source, event_data, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
            sql,
            event.getId(),
            event.getEventId(),
            event.getUserId(),
            event.getEventType().name(),
            event.getServiceSource(),
            toJson(event.getEventData()),
            Timestamp.valueOf(event.getTimestamp())
        );
    }

    private String toJson(Map<String, Map<String, String>> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize eventData", e);
        }
    }

    public List<HistoryEvent> findAll(int page, int size) {
        int offset = page * size;
        String sql =
            "SELECT * FROM history_event ORDER BY timestamp DESC LIMIT ? OFFSET ?";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                HistoryEvent event = new HistoryEvent();
                event.setId(rs.getString("id"));
                event.setEventId(rs.getString("event_id"));
                event.setUserId(rs.getString("user_id"));
                event.setEventType(
                    EventType.valueOf(rs.getString("event_type"))
                );
                event.setServiceSource(rs.getString("service_source"));
                event.setTimestamp(
                    rs.getTimestamp("timestamp").toLocalDateTime()
                );
                event.setEventData(fromJson(rs.getString("event_data")));
                return event;
            },
            size,
            offset
        );
    }

    public List<HistoryEvent> findByUserIdAndTimestampBetween(
        String userId,
        LocalDateTime start,
        LocalDateTime end,
        int page,
        int size
    ) {
        int offset = page * size;

        String sql =
            "SELECT * FROM history_event " +
            "WHERE user_id = ? AND timestamp BETWEEN ? AND ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {
                HistoryEvent event = new HistoryEvent();
                event.setId(rs.getString("id"));
                event.setEventId(rs.getString("event_id"));
                event.setUserId(rs.getString("user_id"));
                event.setEventType(
                    EventType.valueOf(rs.getString("event_type"))
                );
                event.setServiceSource(rs.getString("service_source"));
                event.setTimestamp(
                    rs.getTimestamp("timestamp").toLocalDateTime()
                );

                String eventDataJson = rs.getString("event_data");
                event.setEventData(fromJson(eventDataJson));

                return event;
            },
            userId,
            Timestamp.valueOf(start),
            Timestamp.valueOf(end),
            size,
            offset
        );
    }

    private Map<String, Map<String, String>> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize eventData", e);
        }
    }
}
