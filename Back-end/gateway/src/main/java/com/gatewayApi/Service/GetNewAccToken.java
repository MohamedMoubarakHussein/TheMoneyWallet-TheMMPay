package com.gatewayApi.Service;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetNewAccToken {
        private final HttpHelper httpHelper;

      public String getNewAccToken(String token){
       
       ResponseEntity<String> res = this.httpHelper.getRefToken(token);
        if(!res.getStatusCode().equals(HttpStatusCode.valueOf(200)))
            return "";
      log.info("Done getting New acc token using ref token");
       return  res.getBody();
    }
}
