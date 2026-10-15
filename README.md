# Redis Token Bucket Rate Limiter

This is a Spring Boot application that uses Redis and a token-bucket algorithm to limit API requests.

## Prerequisites

- Java 17+
- Maven
- Redis running on localhost:6379

## Run Redis

### Option 1: Local Redis installed
```bash
redis-server
```

### Option 2: Docker
```bash
docker compose up -d
```

## Start the app
```bash
mvn spring-boot:run
```

## Test the endpoint
```bash
curl -i -H "X-Client-Id: user-123" http://localhost:8080/api/hello
```

## Config

The settings are in `src/main/resources/application.yml`:

```yaml
app:
  rate-limit:
    capacity: 10
    refill-rate-per-second: 2.0
    ttl-seconds: 3600
```

This means:
- bucket capacity: 10 tokens
- refill rate: 2 tokens per second
- each client has its own rate limit bucket stored in Redis
