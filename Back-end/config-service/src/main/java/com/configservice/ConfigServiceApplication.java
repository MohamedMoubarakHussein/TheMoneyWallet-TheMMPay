package com.configservice;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import com.themoneywallet.sharedUtilities.service.EnvLoader;

@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {
    public static void main(String[] args) throws IOException {
        EnvLoader.load("/home/mohamed/Desktop/Projects/TheMoneyWallet-TheMMPay/Back-end/env/dev.env");
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
