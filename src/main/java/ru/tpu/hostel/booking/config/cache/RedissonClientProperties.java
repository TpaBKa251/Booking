package ru.tpu.hostel.booking.config.cache;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "redisson.client")
public record RedissonClientProperties(

        @NotEmpty
        String address,

        String password,

        @NotNull
        Integer database


) {
}
