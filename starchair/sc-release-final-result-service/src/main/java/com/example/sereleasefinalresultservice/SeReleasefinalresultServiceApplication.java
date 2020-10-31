package com.example.sereleasefinalresultservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SeReleasefinalresultServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeReleasefinalresultServiceApplication.class, args);
    }

}
