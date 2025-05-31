package com.themoneywallet.notificationservice.controller;

import com.themoneywallet.notificationservice.utilites.UnifidResponseHandler;
import com.themoneywallet.notificationservice.utilites.ValidationErrorMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final ValidationErrorMessageConverter VErrorConverter;
    private final UnifidResponseHandler uResponseHandler;
}
