package com.example.scmodifyarticleinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ScModifyarticleinfoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScModifyarticleinfoServiceApplication.class, args);
    }

}
