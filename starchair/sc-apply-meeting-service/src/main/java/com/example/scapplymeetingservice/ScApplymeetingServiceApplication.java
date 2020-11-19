package com.example.scapplymeetingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScApplymeetingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScApplymeetingServiceApplication.class, args);
    }

}
