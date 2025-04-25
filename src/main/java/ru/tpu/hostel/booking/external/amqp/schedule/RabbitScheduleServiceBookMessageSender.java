package ru.tpu.hostel.booking.external.amqp.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.tpu.hostel.booking.common.utils.TimeUtil;
import ru.tpu.hostel.booking.config.amqp.RabbitScheduleServiceBookQueueingProperties;
import ru.tpu.hostel.booking.external.amqp.AmqpMessageSender;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Реализация интерфейса {@link AmqpMessageSender} для отправки сообщений микросервису расписаний по RabbitMQ
 */
@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public class RabbitScheduleServiceBookMessageSender implements AmqpMessageSender {

    private static final String NOT_IMPLEMENTED_YET = "Еще не реализовано";

    private static final int HIGH_PRIORITY = 10;

    private static final ObjectWriter WRITER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .setTimeZone(TimeUtil.getTimeZone())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writer();

    private final RabbitTemplate rabbitTemplate;

    private final RabbitScheduleServiceBookQueueingProperties queueProperties;

    public RabbitScheduleServiceBookMessageSender(
            ConnectionFactory connectionFactory,
            RabbitScheduleServiceBookQueueingProperties properties
    ) {
        this.rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitTemplate.setExchange(properties.exchangeName());
        this.rabbitTemplate.setRoutingKey(properties.routingKey());
        this.rabbitTemplate.setObservationEnabled(true);
        this.queueProperties = properties;
    }

    @Override
    public void send(String messageId, Object messagePayload) throws JsonProcessingException {
        send(messageId, messagePayload, rabbitTemplate.getRoutingKey());
    }

    @Override
    public void send(String messageId, Object messagePayload, String routingKey) throws JsonProcessingException {
        MessageProperties messageProperties = getMessageProperties(messageId);
        Message message = new Message(WRITER.writeValueAsBytes(messagePayload), messageProperties);
        rabbitTemplate.send(routingKey, message);
    }

    @Override
    public void send(String messageId, String jsonMessagePayload) {
        log.error(NOT_IMPLEMENTED_YET);
    }

    @Override
    public Message sendAndReceive(String messageId, Object messagePayload) throws JsonProcessingException {
        MessageProperties messageProperties = getMessageProperties(messageId);
        Message message = new Message(WRITER.writeValueAsBytes(messagePayload), messageProperties);
        return rabbitTemplate.sendAndReceive(message);
    }

    private MessageProperties getMessageProperties(String messageId) {
        ZonedDateTime now = TimeUtil.getZonedDateTime();
        long nowMillis = now.toInstant().toEpochMilli();

        return MessagePropertiesBuilder.newInstance()
                .setMessageId(messageId)
                .setCorrelationId(UUID.randomUUID().toString())
                .setPriority(HIGH_PRIORITY)
                .setTimestamp(new Date(nowMillis))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
    }
}
