package com.themoneywallet.historyservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.historyservice.service.shared.EventProducer;
import com.themoneywallet.historyservice.service.shared.RedisService;
import com.themoneywallet.historyservice.utilites.EventHandler;
import com.themoneywallet.historyservice.utilites.HttpHelper;
import com.themoneywallet.historyservice.utilites.UnifidResponseHandler;
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
}
