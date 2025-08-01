package com.walletservice.config;

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
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CustomBean {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate(
        RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        return new RestTemplate(factory);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public RedisCacheManager cacheManager(
        RedisConnectionFactory connectionFactory
    ) {
        return RedisCacheManager.builder(connectionFactory).build();
    }


     @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
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
