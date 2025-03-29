package ru.tpu.hostel.booking.config.otlp;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({OpenTelemetryProperties.class})
@RequiredArgsConstructor
public class OpenTelemetryConfig {

    private final OpenTelemetryProperties properties;

    @Bean
    public SpanExporter otlpSpanExporter() {
        if (Boolean.TRUE.equals(properties.getExportEnabled())) {
            return OtlpGrpcSpanExporter.builder()
                    .setEndpoint(properties.getEndpoint())
                    .setTimeout(properties.getTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .build();
        }
        return SpanExporter.composite();
    }

    @Bean
    public SdkTracerProvider sdkTracerProvider(SpanExporter otlpSpanExporter) {
        return SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(otlpSpanExporter).build())
                .setResource(Resource.getDefault().toBuilder()
                        .put("service.name", properties.getServiceName())
                        .build())
                .build();
    }

}