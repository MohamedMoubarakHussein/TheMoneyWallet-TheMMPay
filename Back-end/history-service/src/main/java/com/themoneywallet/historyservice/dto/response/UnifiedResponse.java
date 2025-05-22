package com.themoneywallet.historyservice.dto.response;

import java.time.Instant;
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
    private Map<String , String> data = new HashMap<>();
    @Builder.Default
    private Instant timeStamp = Instant.now();
    
    private String statusInternalCode;
    private boolean haveData;
    private boolean haveError;

    

}
