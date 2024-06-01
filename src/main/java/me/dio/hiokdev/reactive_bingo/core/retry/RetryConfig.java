package me.dio.hiokdev.reactive_bingo.core.retry;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("reactive-bingo.retry-config")
public record RetryConfig(
        Long maxRetries,
        Long minDuration
) {

    public Duration minDurationSeconds() {
        return Duration.ofSeconds(minDuration);
    }

}
