package com.themoneywallet.historyservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.historyservice.entity.HistoryEvent;
import com.themoneywallet.historyservice.entity.fixed.ResponseKey;
import com.themoneywallet.historyservice.event.EventType;
import com.themoneywallet.historyservice.service.HistoryService;
import com.themoneywallet.historyservice.utilites.UnifidResponseHandler;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {

    private final UnifidResponseHandler uResponseHandler;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    @GetMapping("/user")
    public ResponseEntity<String> getUserHistory(
        @RequestParam String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
        ) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
        ) LocalDateTime endDate,
        @RequestParam(required = false) List<EventType> eventTypes
    ) throws JsonProcessingException {
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        List<HistoryEvent> history = historyService.getUserHistory(
            userId,
            startDate,
            endDate,
            eventTypes,
            page,
            size
        );

        return new ResponseEntity<>(
            this.objectMapper.writeValueAsString(
                    this.uResponseHandler.makResponse(
                            true,
                            this.uResponseHandler.makeRespoData(
                                    ResponseKey.DATA,
                                    Map.of(
                                        "history",
                                        this.objectMapper.writeValueAsString(
                                                history
                                            )
                                    )
                                ),
                            false,
                            null
                        )
                ),
            HttpStatus.OK
        );
    }
}
