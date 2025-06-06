package ru.tpu.hostel.booking.config.cache;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Validated
@ConfigurationProperties(prefix = "redisson.local-cache")
public record RedissonLocalCacheProperties(

        @NotEmpty
        String cacheName,

        @NotNull
        Integer cashSize,

        @NotNull
        @DurationUnit(ChronoUnit.MILLIS)
        Duration timeout,

        @NotNull
        @DurationUnit(ChronoUnit.SECONDS)
        Duration ttl,

        @NotNull
        @DurationUnit(ChronoUnit.SECONDS)
        Duration maxIdle

) {
}
