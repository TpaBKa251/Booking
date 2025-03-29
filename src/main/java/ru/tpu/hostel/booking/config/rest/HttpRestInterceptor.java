package ru.tpu.hostel.booking.config.rest;

import feign.RequestInterceptor;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Интерсептор для добавления в заголовок REST запросов информации о трассировке
 */
@Slf4j
@Configuration
public class HttpRestInterceptor {

    @Bean
    public RequestInterceptor tracingHttpRequestInterceptor() {
        return requestTemplate -> {
            Span currentSpan = Span.current();
            SpanContext spanContext = currentSpan.getSpanContext();

            if (spanContext.isValid()) {
                String traceparent = String.format("00-%s-%s-01",
                        spanContext.getTraceId(),
                        spanContext.getSpanId());
                requestTemplate.header("traceparent", traceparent);
            }
        };
    }
}
