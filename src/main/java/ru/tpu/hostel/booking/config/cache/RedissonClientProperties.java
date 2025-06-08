package ru.tpu.hostel.booking.config.cache;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "redisson.client")
@Data
public class RedissonClientProperties {

    @NotEmpty
    private String address = "redis://localhost:6379";

    private String password = null;

    @NotNull
    private Integer database = 0;

    @NotNull
    private Integer timeout = 5000;

    @NotNull
    private Integer connectionTimeout = 10_000;

    @NotNull
    private Integer retryAttempts = 2;

    @NotNull
    private Integer connectionPoolSize = 30;

    @NotNull
    private Integer connectionMinimumIdleSize = 10;

    @NotNull
    private Integer subscriptionConnectionPoolSize = 10;

    @NotNull
    private Integer subscriptionConnectionMinimumIdleSize = 1;

    @NotNull
    private Integer idleConnectionTimeout = 10_000;

    @NotNull
    private Integer pingConnectionInterval = 30_000;

    @NotNull
    private Boolean keepAlive = true;

    @NotNull
    private Boolean tcpNoDelay = true;

    @NotEmpty
    private String clientName = "RedissonClient";

}
