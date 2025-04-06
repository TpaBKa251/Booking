package ru.tpu.hostel.booking.config.amqp;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Свойства очереди для отправки сообщений микросервису расписаний и получения от него ответа по RabbitMQ
 *
 * @param exchangeName    Имя обменника
 * @param queueReplyName  Имя очереди для ответа
 * @param routingKey      Имя ключа маршрутизации для отправки
 * @param replyRoutingKey Имя ключа маршрутизации для получения ответа
 */
@Validated
@ConfigurationProperties(prefix = "queueing.schedules-service")
public record RabbitScheduleServiceQueueingProperties(

        @NotEmpty
        String exchangeName,

        @NotEmpty
        String queueReplyName,

        @NotEmpty
        String routingKey,

        @NotEmpty
        String replyRoutingKey

) {
}
