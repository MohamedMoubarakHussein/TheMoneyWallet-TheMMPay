package com.themoneywallet.sharedUtilities.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UnifiedResponseBuilder {

    private static final String SERIALIZATION_ERROR_CODE = "SER001";
    private static final String SERIALIZATION_ERROR_MESSAGE = "Data serialization failed";

    private final ObjectMapper objectMapper;
    private final UnifiedResponse.UnifiedResponseBuilder responseBuilder = UnifiedResponse.builder();
    private final Map<String, Map<String, String>> data = new HashMap<>();
    private HttpStatus status;

    public UnifiedResponseBuilder(ObjectMapper objectMapper, HttpStatus status) {
        this.objectMapper = objectMapper;
        this.status = status;
        responseBuilder.timeStamp(LocalDateTime.now());
    }

    public UnifiedResponseBuilder withData(String key, Object message) {
        try {
            String serializedMessage = objectMapper.writeValueAsString(message);
            data.put(ResponseKey.DATA.toString(), Map.of(key, serializedMessage));
            String structureInfo = String.format("Retrieve data using key: %s. Deserialize to: %s",
                    key, message.getClass().getSimpleName());
            data.put(ResponseKey.INFO.toString(), Map.of("structure", structureInfo));
            responseBuilder.haveData(true);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize success message for key: {}", key, e);
            withError("error", SERIALIZATION_ERROR_MESSAGE, SERIALIZATION_ERROR_CODE, "String");
        }
        return this;
    }

    public UnifiedResponseBuilder withError(String key, Object message, String code, String deserializationInfo) {
        String structureInfo = String.format("Error occurred. Retrieve data using key (%s). Deserialize to: %s",
                key, deserializationInfo);
        data.put(ResponseKey.INFO.toString(), Map.of("structure", structureInfo));

        try {
            String serializedMessage = objectMapper.writeValueAsString(message);
            data.put(ResponseKey.ERROR.toString(), Map.of(key, serializedMessage));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize error message for key: {}", key, e);
            data.put(ResponseKey.ERROR.toString(), Map.of(key, "Internal serialization error. Contact support with code: " + SERIALIZATION_ERROR_CODE));
            responseBuilder.statusInternalCode(SERIALIZATION_ERROR_CODE);
        }
        responseBuilder.haveError(true);
        responseBuilder.statusInternalCode(code);
        return this;
    }

    public UnifiedResponseBuilder withSuccessNoBody(String key) {
        data.put(ResponseKey.DATA.toString(), Map.of(key, "No data"));
        data.put(ResponseKey.INFO.toString(), Map.of("structure",
                String.format("Retrieve data using key: %s. Deserialize to: No data", key)));
        responseBuilder.haveData(false);
        return this;
    }

    public ResponseEntity<UnifiedResponse> build(HttpStatus status) {
        responseBuilder.data(data);
        return ResponseEntity.status(status).body(responseBuilder.build());
    }
}
