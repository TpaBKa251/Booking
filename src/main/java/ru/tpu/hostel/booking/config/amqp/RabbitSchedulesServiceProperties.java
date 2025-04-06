package ru.tpu.hostel.booking.config.amqp;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Свойства для подключения к брокеру сообщений RabbitMQ
 *
 * @param username          имя пользователя (логин)
 * @param password          пароль
 * @param virtualHost       виртуальный хост
 * @param addresses         адресы
 * @param connectionTimeout таймаут для подключения
 */
@Validated
@ConfigurationProperties(prefix = "rabbitmq.schedules-service")
public record RabbitSchedulesServiceProperties(

        @NotEmpty
        String username,

        @NotEmpty
        String password,

        @NotEmpty
        String virtualHost,

        @NotEmpty
        String addresses,

        @NotNull
        @DurationUnit(ChronoUnit.MILLIS)
        Duration connectionTimeout

) {
}
