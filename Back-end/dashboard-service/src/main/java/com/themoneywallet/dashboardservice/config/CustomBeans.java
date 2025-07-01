package com.themoneywallet.dashboardservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomBeans {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
