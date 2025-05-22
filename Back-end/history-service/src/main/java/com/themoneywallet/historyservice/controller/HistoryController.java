package com.themoneywallet.historyservice.controller;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.historyservice.service.HistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {
    
    private final HistoryService historyService;
  
    /*
    @GetMapping("/users/{userId}")
    public ResponseEntity<HistoryResponse> getUserHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> eventTypes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        // Create filter object
        HistoryFilter filter = HistoryFilter.builder()
            .eventTypes(eventTypes)
            .startDate(startDate)
            .endDate(endDate)
            .build();
        
        // Create pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        // Get history
        HistoryResponse response = historyService.getUserHistory(userId, filter, pageable);
        
        return ResponseEntity.ok(response);
    }
    
     */
    
   
}