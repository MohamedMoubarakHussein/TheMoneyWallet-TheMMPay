package com.themoneywallet.dashboardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.themoneywallet"})
@EnableDiscoveryClient
@EnableKafka
@EnableTransactionManagement
public class DashboardserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardserviceApplication.class, args);
    }
}
