package com.configservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import com.configservice.config.env.EnvPropertySourceFactory;

@Configuration
public class DotenvConfig {

    @Configuration
    @Profile("dev")
    @PropertySource(value = "file:${user.dir}/config-service/env/dev.env", 
                   factory = EnvPropertySourceFactory.class,
                   ignoreResourceNotFound = true)
    public static class DevDotenvConfig {
    }

    @Configuration
    @Profile("prod")
    @PropertySource(value = "file:${user.dir}/config-service/env/prod.env", 
                   factory = EnvPropertySourceFactory.class,
                   ignoreResourceNotFound = true)
    public static class ProdDotenvConfig {
    }
}
