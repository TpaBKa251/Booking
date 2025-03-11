package ru.tpu.hostel.booking.config.amqp;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Свойства для подключения к брокеру сообщений RabbitMQ
 */
@Data
@Validated
@ConfigurationProperties(prefix = "rabbitmq.schedules-service")
public class RabbitSchedulesServiceProperties {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String virtualHost;

    @NotEmpty
    private String addresses;

    @NotNull
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectionTimeout;

}
