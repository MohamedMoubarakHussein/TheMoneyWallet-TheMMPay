package com.themoneywallet.historyservice.dto.response;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Scope(
    value = WebApplicationContext.SCOPE_SESSION,
    proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class UnifiedResponse {

    @Builder.Default
    private Map<String, Map<String, String>> data = new HashMap<>();

    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();

    private String statusInternalCode;
    private boolean haveData;
    private boolean haveError;
}
