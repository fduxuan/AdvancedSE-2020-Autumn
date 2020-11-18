package com.example.scadminmanageconferenceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScAdminmanageconferenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScAdminmanageconferenceServiceApplication.class, args);
    }

}
