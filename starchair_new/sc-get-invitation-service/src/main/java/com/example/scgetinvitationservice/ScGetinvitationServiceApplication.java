package com.example.scgetinvitationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScGetinvitationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScGetinvitationServiceApplication.class, args);
    }

}
