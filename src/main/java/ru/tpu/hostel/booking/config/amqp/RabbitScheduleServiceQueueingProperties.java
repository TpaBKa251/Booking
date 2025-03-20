package ru.tpu.hostel.booking.config.amqp;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Свойства очереди для отправки сообщений микросервису расписаний и получения от него ответа по RabbitMQ
 */
@Data
@Validated
@ConfigurationProperties(prefix = "queueing.schedules-service")
public class RabbitScheduleServiceQueueingProperties {

    /**
     * Имя обменника
     */
    @NotEmpty
    private String exchangeName;

    /**
     * Имя очереди для ответа
     */
    @NotEmpty
    private String queueReplyName;

    /**
     * Имя ключа маршрутизации для отправки
     */
    @NotEmpty
    private String routingKey;

    /**
     * Имя ключа маршрутизации для получения ответа
     */
    @NotEmpty
    private String replyRoutingKey;

}
