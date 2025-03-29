package ru.tpu.hostel.booking.config.otlp;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@Validated
@ConfigurationProperties(prefix = "otlp.tracing")
public class OpenTelemetryProperties {

    @NotNull
    private Boolean exportEnabled;

    @NotNull
    private String endpoint;

    @DurationUnit(ChronoUnit.MILLIS)
    @NotNull
    private Duration timeout;

    @NotNull
    private String serviceName;

}
