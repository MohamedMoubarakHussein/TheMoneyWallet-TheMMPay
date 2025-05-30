package com.walletservice.utilites;

import com.walletservice.dto.response.UnifiedResponse;
import java.time.Instant;
import java.time.LocalDateTime;
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
        return new UnifiedResponse()
            .builder()
            .timeStamp(LocalDateTime.now())
            .haveData(haveData)
            .data(data)
            .haveError(haveError)
            .statusInternalCode(statusInternalCode)
            .build();
    }
}
