package com.example.sepapersubmitservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SePapersubmitServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SePapersubmitServiceApplication.class, args);
    }

}
