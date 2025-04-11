package com.themoneywallet.authenticationservice.dto.response;

import java.util.HashMap;
import java.util.List;
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
    private Map<String , Object> responsMap = new HashMap<>();
    @Builder.Default
    private Map<String, List<String>> neastedResponseMap = new HashMap<>();

}
