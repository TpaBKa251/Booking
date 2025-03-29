package ru.tpu.hostel.booking.config.otlp;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("booking-service");
    }

    @Bean
    public OpenTelemetry openTelemetry(SpanExporter spanExporter) {
        Resource resource = Resource.builder()
                .put("service.name", "booking-service")
                .build();

        // Настройка провайдера трассировок
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build()) // Используем Batch вместо Simple
                .setResource(resource)
                .build();

        // Настройка провайдера метрик
//        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
//                .registerMetricReader(PeriodicMetricReader.builder(metricExporter)
//                        .setInterval(Duration.ofSeconds(30))
//                        .build())
//                .setResource(resource)
//                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                //.setMeterProvider(meterProvider)
                .buildAndRegisterGlobal(); // Регистрируем как глобальный инстанс
    }

//    @Bean
//    public OtlpGrpcMetricExporter otlpMetricExporter() {
//        return OtlpGrpcMetricExporter.builder()
//                .setEndpoint("http://localhost:4317")
//                .setTimeout(2, TimeUnit.SECONDS)
//                .build();
//    }

    @Bean
    public OtlpGrpcSpanExporter otlpSpanExporter() {
        return OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317")
                .setTimeout(2, TimeUnit.SECONDS) // Добавляем таймаут
                .build();
    }
}