package com.example.ratelimiter.controller;

import com.example.ratelimiter.service.TokenBucketRateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class RateLimitController {

    private final TokenBucketRateLimiterService rateLimiter;

    public RateLimitController(TokenBucketRateLimiterService rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello(@RequestHeader(value = "X-Client-Id", defaultValue = "anonymous") String clientId) {
        String bucketKey = "rate-limit:" + clientId;

        boolean allowed = rateLimiter.tryConsume(bucketKey);

        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }

        return ResponseEntity.ok("Hello from protected API");
    }
}
