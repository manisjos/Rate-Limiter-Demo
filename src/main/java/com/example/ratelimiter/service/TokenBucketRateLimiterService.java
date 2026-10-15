package com.example.ratelimiter.service;

import com.example.ratelimiter.config.RateLimitProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenBucketRateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;

    public TokenBucketRateLimiterService(StringRedisTemplate redisTemplate, RateLimitProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public boolean tryConsume(String key) {
        return tryConsume(key, properties.getCapacity(), properties.getRefillRatePerSecond(), properties.getTtlSeconds());
    }

    public boolean tryConsume(String key, int capacity, double refillRatePerSecond, long ttlSeconds) {
        long nowMs = System.currentTimeMillis();

        String script = """
            local bucketKey = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refillRate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3]) / 1000.0
            local ttlSeconds = tonumber(ARGV[4])

            local tokens = redis.call('HGET', bucketKey, 'tokens')
            local lastRefill = redis.call('HGET', bucketKey, 'last_refill')

            if tokens == false then
                tokens = capacity
            else
                tokens = tonumber(tokens)
            end

            if lastRefill == false then
                lastRefill = now
            else
                lastRefill = tonumber(lastRefill)
            end

            local elapsed = math.max(0, now - lastRefill)
            local refill = elapsed * refillRate

            if refill > 0 then
                tokens = tokens + refill
                if tokens > capacity then
                    tokens = capacity
                end
                lastRefill = now
            end

            if tokens >= 1 then
                tokens = tokens - 1
                redis.call('HSET', bucketKey, 'tokens', tostring(tokens))
                redis.call('HSET', bucketKey, 'last_refill', tostring(lastRefill))
                redis.call('EXPIRE', bucketKey, ttlSeconds)
                return 1
            else
                redis.call('HSET', bucketKey, 'tokens', tostring(tokens))
                redis.call('HSET', bucketKey, 'last_refill', tostring(lastRefill))
                redis.call('EXPIRE', bucketKey, ttlSeconds)
                return 0
            end
            """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);

        Long allowed = redisTemplate.execute(
                redisScript,
                List.of(key),
                String.valueOf(capacity),
                String.valueOf(refillRatePerSecond),
                String.valueOf(nowMs),
                String.valueOf(ttlSeconds)
        );

        return allowed != null && allowed == 1L;
    }
}
