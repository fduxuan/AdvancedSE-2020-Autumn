package com.example.scstartsubmitservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScStartsubmitServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScStartsubmitServiceApplication.class, args);
    }

}
