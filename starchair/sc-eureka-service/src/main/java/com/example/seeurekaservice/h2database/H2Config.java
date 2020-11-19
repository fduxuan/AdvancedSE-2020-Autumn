package com.example.seeurekaservice.h2database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.h2.tools.Server;
import java.sql.SQLException;

@Configuration
public class H2Config {
    //"-web", "-webAllowOthers","-browser",
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers",  "-tcpPort", "9090");
    }
}

