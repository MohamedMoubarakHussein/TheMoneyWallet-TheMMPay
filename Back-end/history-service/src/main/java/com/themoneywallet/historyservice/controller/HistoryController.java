package com.themoneywallet.historyservice.controller;

import com.themoneywallet.historyservice.utilites.UnifidResponseHandler;
import com.themoneywallet.historyservice.utilites.ValidationErrorMessageConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Slf4j
public class HistoryController {

    private final ValidationErrorMessageConverter VErrorConverter;
    private final UnifidResponseHandler uResponseHandler;
}
