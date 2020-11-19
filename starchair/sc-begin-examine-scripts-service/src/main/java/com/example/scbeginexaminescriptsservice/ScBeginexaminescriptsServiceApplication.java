package com.example.scbeginexaminescriptsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScBeginexaminescriptsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScBeginexaminescriptsServiceApplication.class, args);
	}

}
