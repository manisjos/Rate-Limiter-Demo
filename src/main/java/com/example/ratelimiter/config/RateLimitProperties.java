package com.example.ratelimiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {
    private int capacity = 10;
    private double refillRatePerSecond = 2.0;
    private long ttlSeconds = 3600;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getRefillRatePerSecond() {
        return refillRatePerSecond;
    }

    public void setRefillRatePerSecond(double refillRatePerSecond) {
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}
