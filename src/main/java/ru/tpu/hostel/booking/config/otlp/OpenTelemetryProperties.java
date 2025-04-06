package ru.tpu.hostel.booking.config.otlp;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Свойства для OpenTelemetry трассировки
 *
 * @param exportEnabled включает экспорт трассировки
 * @param endpoint      эндроинт, куда экспортировать трассировку
 * @param timeout       таймаут для экспорта
 * @param serviceName   имя сервиса
 */
@Validated
@ConfigurationProperties(prefix = "otlp.tracing")
public record OpenTelemetryProperties(

        @NotNull
        Boolean exportEnabled,

        @NotEmpty
        String endpoint,

        @DurationUnit(ChronoUnit.MILLIS)
        @NotNull
        Duration timeout,

        @NotEmpty
        String serviceName

) {
}
