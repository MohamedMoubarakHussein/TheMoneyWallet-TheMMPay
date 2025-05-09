package com.themoneywallet.usermanagmentservice.utilite;


import java.util.HashMap;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.themoneywallet.usermanagmentservice.event.UserSingUpEvent;

@Configuration
public class KafkaConsumer {

    @Bean
    public ConsumerFactory<String, UserSingUpEvent> consumerFactory() {
        JsonDeserializer<UserSingUpEvent> deserializer = new JsonDeserializer<>(UserSingUpEvent.class);
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                new HashMap<>(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserSingUpEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserSingUpEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
