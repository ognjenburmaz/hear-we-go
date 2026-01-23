package com.streaming.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RecommendationsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendationsServiceApplication.class, args);
	}

}
