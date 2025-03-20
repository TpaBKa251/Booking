package ru.tpu.hostel.booking.rabbit.amqp.schedule;

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
import ru.tpu.hostel.booking.rabbit.amqp.AmqpMessageSender;
import ru.tpu.hostel.booking.config.amqp.RabbitScheduleServiceQueueingProperties;
import ru.tpu.hostel.booking.exception.SlotNotFoundException;
import ru.tpu.hostel.booking.utils.TimeNow;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Реализация интерфейса {@link AmqpMessageSender} для отправки сообщений микросервису расписаний по RabbitMQ
 */
@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public class RabbitScheduleServiceMessageSender implements AmqpMessageSender {

    private static final String NOT_IMPLEMENTED_YET = "Еще не реализовано";

    private static final int HIGH_PRIORITY = 10;

    private static final ObjectWriter WRITER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .setTimeZone(TimeNow.getTimeZone())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writer();

    private final RabbitTemplate rabbitTemplate;

    private final RabbitScheduleServiceQueueingProperties queueProperties;

    public RabbitScheduleServiceMessageSender(
            ConnectionFactory schedulesServiceConnectionFactory,
            RabbitScheduleServiceQueueingProperties properties
    ) {
        this.rabbitTemplate = new RabbitTemplate(schedulesServiceConnectionFactory);
        this.rabbitTemplate.setExchange(properties.getExchangeName());
        this.rabbitTemplate.setRoutingKey(properties.getRoutingKey());
        this.queueProperties = properties;
    }

    @Override
    public void send(String messageId, Object messagePayload) throws JsonProcessingException {
        MessageProperties messageProperties = getMessageProperties(messageId);
        Message message = new Message(WRITER.writeValueAsBytes(messagePayload), messageProperties);
        rabbitTemplate.send(message);
    }

    @Override
    public void send(String messageId, Object messagePayload, String routingKey) throws JsonProcessingException {
        log.error(NOT_IMPLEMENTED_YET);
    }

    @Override
    public void send(String messageId, String jsonMessagePayload) {
        log.error(NOT_IMPLEMENTED_YET);
    }

    @Override
    public Message sendAndReceive(String messageId, Object messagePayload) throws JsonProcessingException {
        MessageProperties messageProperties = getMessageProperties(messageId);
        Message message = new Message(WRITER.writeValueAsBytes(messagePayload), messageProperties);

        Message replyMessage = rabbitTemplate.sendAndReceive(message);

        if (replyMessage == null
                || replyMessage.getBody() == null
                || replyMessage.getBody().length == 0
                || "null".equals(new String(replyMessage.getBody()))) {
            throw new SlotNotFoundException();
        }

        return replyMessage;
    }

    private MessageProperties getMessageProperties(String messageId) {
        ZonedDateTime now = TimeNow.getZonedDateTime();
        long nowMillis = now.toInstant().toEpochMilli();

        return MessagePropertiesBuilder.newInstance()
                .setMessageId(messageId)
                .setCorrelationId(UUID.randomUUID().toString())
                .setPriority(HIGH_PRIORITY)
                .setTimestamp(new Date(nowMillis))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setReplyTo(queueProperties.getQueueReplyName())
                .build();
    }
}
