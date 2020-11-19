package com.example.scstartsubmitservice.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Some properties about JWT.
 * You can change the value in `application.properties`.
 *
 * @author LBW
 */
@Component
@ConfigurationProperties(prefix = "jwt.token")
public class JwtConfigProperties {
    private int validity;
    private String secret;

    public JwtConfigProperties(){
        this.validity = 18000000;
        this.secret = "FdseFdse2020";
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
