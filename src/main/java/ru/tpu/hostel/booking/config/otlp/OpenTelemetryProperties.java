package ru.tpu.hostel.booking.config.otlp;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
//@ConfigurationProperties(prefix = "otlp.tracing")
public class OpenTelemetryProperties {

    private Boolean enabled;

    private Double samplingProbability;
}
