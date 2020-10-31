package com.example.scexaminemanuscripts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScExaminemanuscriptsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScExaminemanuscriptsApplication.class, args);
    }

}
