package com.example.scgetinvitionalsentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScGetinvitionalsentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScGetinvitionalsentServiceApplication.class, args);
    }

}
