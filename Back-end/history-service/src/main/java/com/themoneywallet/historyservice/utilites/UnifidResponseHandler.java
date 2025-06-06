package com.themoneywallet.historyservice.utilites;

import com.themoneywallet.historyservice.dto.response.UnifiedResponse;
import com.themoneywallet.historyservice.entity.fixed.ResponseKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

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
