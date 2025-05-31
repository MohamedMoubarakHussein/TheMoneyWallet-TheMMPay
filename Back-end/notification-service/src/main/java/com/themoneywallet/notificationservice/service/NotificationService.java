package com.themoneywallet.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.notificationservice.service.shared.EventProducer;
import com.themoneywallet.notificationservice.service.shared.RedisService;
import com.themoneywallet.notificationservice.utilites.EventHandler;
import com.themoneywallet.notificationservice.utilites.HttpHelper;
import com.themoneywallet.notificationservice.utilites.UnifidResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EventProducer eventProducer;
    private final UnifidResponseHandler uResponseHandler;
    private final EventHandler eventHandler;
    private final HttpHelper httpHelper;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;
}
