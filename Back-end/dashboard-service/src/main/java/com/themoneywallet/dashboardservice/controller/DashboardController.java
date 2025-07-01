package com.themoneywallet.dashboardservice.controller;

import com.themoneywallet.dashboardservice.dto.response.UnifiedResponse;
import com.themoneywallet.dashboardservice.service.implementation.DashboardQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DashboardController {
//TODO make global exception handdler return the error in unified formate
    private final DashboardQueryService dashboardQueryService;

    @GetMapping("/user")
    public ResponseEntity<UnifiedResponse> getUserDashboard(
        @RequestHeader("Authorization") String token ) {
        return this.dashboardQueryService.getUserDashboard(token);
    }


}
