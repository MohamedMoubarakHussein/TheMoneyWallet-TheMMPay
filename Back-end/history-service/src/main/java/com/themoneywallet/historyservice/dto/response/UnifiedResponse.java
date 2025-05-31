package com.themoneywallet.historyservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnifiedResponse {

    @Builder.Default
    private Map<String, Map<String, String>> data = new HashMap<>();

    @Builder.Default
    private LocalDateTime timeStamp = LocalDateTime.now();

    private String statusInternalCode;
    private boolean haveData;
    private boolean haveError;
}
