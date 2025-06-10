package com.themoneywallet.dashboardservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.themoneywallet.dashboardservice.dto.response.UnifiedResponse;
import com.themoneywallet.dashboardservice.service.implementation.DashboardQueryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor  
@Slf4j  
@Validated 
public class DashboardController {

    private final DashboardQueryService dashboardQueryService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UnifiedResponse> getUserDashboard(HttpServletRequest req, @RequestHeader("Authorization") String token,@CookieValue("refreshToken") String refToken) {
        return this.dashboardQueryService.getUserDashboard(token, refToken);
    }
    
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<UnifiedResponse> getUserSummary(HttpServletRequest req, @RequestHeader("Authorization") String token,@CookieValue("refreshToken") String refToken) {      
        return  dashboardQueryService.getUserSummary(token, refToken);
    }
}