package com.example.scinvitepcservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScInvitepcServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScInvitepcServiceApplication.class, args);
	}

}
