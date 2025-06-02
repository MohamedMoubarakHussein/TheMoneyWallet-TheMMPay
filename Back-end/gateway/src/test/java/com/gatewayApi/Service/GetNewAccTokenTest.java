package com.gatewayApi.Service;

import java.time.Clock;
import java.time.ZoneId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class GetNewAccTokenTest {

    @Mock
    private HttpHelper httpHelper;

    @InjectMocks
    private GetNewAccToken getNewAccToken;

    @BeforeEach
    void setup() {
        getNewAccToken = new GetNewAccToken(httpHelper);
    }

    @Test
    void testGetNewAccToken_Success() {
        String token = "ref-token";
        String expectedAccessToken = "new-access-token";

        ResponseEntity<String> response = new ResponseEntity<>(
            expectedAccessToken,
            HttpStatus.OK
        );
        Mockito.when(httpHelper.getRefToken(token)).thenReturn(response);

        String actual = getNewAccToken.getNewAccToken(token);

        Assertions.assertEquals(expectedAccessToken, actual);
    }
}
