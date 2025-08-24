package com.themoneywallet.transactionservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.themoneywallet.sharedUtilities.utilities.EventHandler;
import com.themoneywallet.sharedUtilities.utilities.SerializationDeHalper;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.sharedUtilities.utilities.ValidtionRequestHandler;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomBean {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        return new RestTemplate(factory);
    }
     @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    @Bean
    public UnifidResponseHandler unifidResponseHandler(ObjectMapper objectMapper){
        return new UnifidResponseHandler(objectMapper);
    }

    @Bean
    public SerializationDeHalper serializationDeHalper(ObjectMapper objectMapper){
        return new SerializationDeHalper(objectMapper);
    }

    @Bean 
    public ValidtionRequestHandler validtionRequestHandler(UnifidResponseHandler unifidResponseHandler){
        return new ValidtionRequestHandler(unifidResponseHandler);
    }
    @Bean
    public EventHandler eventHandler(ObjectMapper objectMapper){
        return new EventHandler(objectMapper);
    }

}