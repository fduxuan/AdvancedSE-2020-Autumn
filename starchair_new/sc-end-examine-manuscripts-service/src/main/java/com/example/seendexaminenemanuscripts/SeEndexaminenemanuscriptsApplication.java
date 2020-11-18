package com.example.seendexaminenemanuscripts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SeEndexaminenemanuscriptsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeEndexaminenemanuscriptsApplication.class, args);
    }
}
