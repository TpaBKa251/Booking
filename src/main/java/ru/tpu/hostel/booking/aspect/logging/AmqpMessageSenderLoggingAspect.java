package ru.tpu.hostel.booking.aspect.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования отправки и получения сообщений через RabbitMQ
 */
@Aspect
@Component
@Slf4j
public class AmqpMessageSenderLoggingAspect {

    private static final ObjectWriter WRITER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .writer();

    @Around("execution(* ru.tpu.hostel.booking.external.amqp.AmqpMessageSender.send(..))")
    public Object logSendMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String messageId = (String) args[0];
        Object messagePayload = args[1];

        String payloadJson = safeMapToJson(messagePayload);
        log.info("Отправка сообщения: messageId={}, payload={}", messageId, payloadJson);

        Object result = joinPoint.proceed();

        log.info("Сообщение отправлено: messageId={}", messageId);
        return result;
    }

    @Around("execution(* ru.tpu.hostel.booking.external.amqp.AmqpMessageSender.sendAndReceive(..))")
    public Object logSendAndReceive(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String messageId = (String) args[0];
        Object messagePayload = args[1];

        String payloadJson = safeMapToJson(messagePayload);
        log.info("RPC-запрос: messageId={}, payload={}", messageId, payloadJson);

        Object result = joinPoint.proceed();

        if (result instanceof Message replyMessage) {
            String responseBody = replyMessage.getBody() != null
                    ? new String(replyMessage.getBody())
                    : "<пусто>";
            log.info("RPC-ответ: messageId={}, response={}", messageId, responseBody);
        } else {
            log.warn("RPC-ответ пустой: messageId={}", messageId);
        }

        return result;
    }

    private String safeMapToJson(Object obj) {
        try {
            return WRITER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации в JSON: {}", e.getMessage(), e);
            return "<ошибка сериализации>";
        }
    }
}

