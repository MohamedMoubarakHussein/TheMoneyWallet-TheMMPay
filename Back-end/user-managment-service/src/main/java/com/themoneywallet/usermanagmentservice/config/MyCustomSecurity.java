package com.themoneywallet.usermanagmentservice.config;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("mySecurity")
@Slf4j
public class MyCustomSecurity {

    public boolean checkCustomAccess( ) {
        log.info("xxcc a  ");
        return true;
    }
}