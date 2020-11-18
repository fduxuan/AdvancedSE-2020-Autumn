package com.example.scgetmymeetingapplicationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScGetmymeetingapplicationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScGetmymeetingapplicationServiceApplication.class, args);
	}

}
