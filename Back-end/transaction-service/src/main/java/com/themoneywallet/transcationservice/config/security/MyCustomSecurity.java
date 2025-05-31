package com.themoneywallet.transcationservice.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("mySecurity")
@Slf4j
public class MyCustomSecurity {

    public boolean checkCustomAccess() {
        log.info("xxcc a  ");
        return true;
    }
}
