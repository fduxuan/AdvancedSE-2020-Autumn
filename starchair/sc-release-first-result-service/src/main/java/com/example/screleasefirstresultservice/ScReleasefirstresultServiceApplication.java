package com.example.screleasefirstresultservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScReleasefirstresultServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScReleasefirstresultServiceApplication.class, args);
    }

}
