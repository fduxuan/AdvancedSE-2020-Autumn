package com.example.scdiscussionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScDiscussionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScDiscussionServiceApplication.class, args);
	}

}
