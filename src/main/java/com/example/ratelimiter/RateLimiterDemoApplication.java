package com.example.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RateLimiterDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimiterDemoApplication.class, args);
    }
}
