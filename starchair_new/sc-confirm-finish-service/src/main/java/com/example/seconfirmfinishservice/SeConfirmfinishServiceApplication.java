package com.example.seconfirmfinishservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SeConfirmfinishServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeConfirmfinishServiceApplication.class, args);
    }

}
