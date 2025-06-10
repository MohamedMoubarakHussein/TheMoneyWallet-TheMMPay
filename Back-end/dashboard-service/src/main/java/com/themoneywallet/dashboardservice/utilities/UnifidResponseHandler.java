package com.themoneywallet.dashboardservice.utilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.themoneywallet.dashboardservice.dto.response.UnifiedResponse;
import com.themoneywallet.dashboardservice.entity.fixed.ResponseKey;

@Component
public class UnifidResponseHandler {

    public UnifiedResponse makResponse(
        boolean haveData,
        Map<String, Map<String, String>> data,
        boolean haveError,
        String statusInternalCode
    ) {
        return UnifiedResponse.builder()
            .timeStamp(LocalDateTime.now())
            .haveData(haveData)
            .data(data)
            .haveError(haveError)
            .statusInternalCode(statusInternalCode)
            .build();
    }

    public Map<String, Map<String, String>> makeRespoData(
        ResponseKey key,
        Map<String, String> insideMp
    ) {
        Map<String, Map<String, String>> data = new HashMap<>();
        data.computeIfAbsent(key.toString(), k -> new HashMap<>(insideMp));
        return data;
    }
}
