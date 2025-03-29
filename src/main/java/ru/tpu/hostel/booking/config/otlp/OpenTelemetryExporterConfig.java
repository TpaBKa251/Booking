package ru.tpu.hostel.booking.config.otlp;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryExporterConfig {

    //@Bean
    public SpanExporter otlpSpanExporter() {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint("http://localhost:4317") // Если Tempo в Docker, может быть другим
                .build();
    }
}
