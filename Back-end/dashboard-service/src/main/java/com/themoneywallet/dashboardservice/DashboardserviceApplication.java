package com.themoneywallet.dashboardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableTransactionManagement
public class DashboardserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DashboardserviceApplication.class, args);
	}

}
