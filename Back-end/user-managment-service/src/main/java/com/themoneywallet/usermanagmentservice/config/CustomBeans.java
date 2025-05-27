package com.themoneywallet.usermanagmentservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class CustomBeans {
    
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

     @Bean
    public ObjectMapper objectMapper() {
            return new ObjectMapper();
    }


    
}
