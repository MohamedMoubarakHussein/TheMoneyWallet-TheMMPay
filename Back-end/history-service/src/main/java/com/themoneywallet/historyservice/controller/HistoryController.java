package com.themoneywallet.historyservice.controller;

import com.themoneywallet.historyservice.dto.response.UnifiedResponse;
import com.themoneywallet.historyservice.entity.HistoryEvent;
import com.themoneywallet.historyservice.event.EventType;
import com.themoneywallet.historyservice.service.HistoryService;
import com.themoneywallet.historyservice.utilites.UnifidResponseHandler;
import com.themoneywallet.historyservice.utilites.ValidationErrorMessageConverter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
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

    private final ValidationErrorMessageConverter VErrorConverter;
    private final UnifidResponseHandler uResponseHandler;
    private final HistoryService historyService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<String> getUserHistory(
        @PathVariable String userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
        ) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
        ) LocalDateTime endDate,
        @RequestParam(required = false) List<EventType> eventTypes
    ) {
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
            eventTypes
        );

        return ResponseEntity.ok()
            .body(
                this.uResponseHandler.makResponse(
                        true,
                        Map.of(
                            "data",
                            Map.of("allHistory", history.toString())
                        ),
                        false,
                        null
                    ).toString()
            );
    }
}
